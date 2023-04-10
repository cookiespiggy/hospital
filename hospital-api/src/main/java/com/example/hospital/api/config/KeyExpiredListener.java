package com.example.hospital.api.config;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.example.hospital.api.db.dao.MisUserDao;
import com.example.hospital.api.db.dao.VideoDiagnoseDao;
import com.example.hospital.api.socket.WebSocketService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.trtc.v20190722.models.DismissRoomByStrRoomIdRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {

    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private MisUserDao misUserDao;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Value("${tencent.cloud.secretId}")
    private String cloudSecretId;

    @Value("${tencent.cloud.secretKey}")
    private String cloudSecretKey;

    @Value("${tencent.trtc.appId}")
    private Long trtcAppId;

    @Resource
    private VideoDiagnoseDao videoDiagnoseDao;



    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        //跟视频问诊有关的缓存被销毁
        if(key.startsWith("video_diagnose_")) {

            String temp = key.substring(key.lastIndexOf("_") + 1);
            //视频问诊订单号
            int currentOrder = Integer.parseInt(temp.split("#")[0]);
            //医生ID
            int doctorId = Integer.parseInt(temp.split("#")[1]);

            //根据doctorId查找userId，推送消息时候使用
            int userId = misUserDao.searchUserId(new HashMap() {{
                put("refId", doctorId);
                put("job", "医生");
            }});

            //医生上线缓存Key
            String onlineKey = "online_doctor_" + doctorId;


            //问诊开始缓存过期
            if (key.startsWith("video_diagnose_start_")) {
                //更新上线缓存中的当前问诊状态
                redisTemplate.opsForHash().put(onlineKey, "currentStatus", 2);


                String roomId = IdUtil.simpleUUID().toUpperCase();
                redisTemplate.opsForHash().put(onlineKey, "roomId", roomId);

                //更新数据库记录
                HashMap param = new HashMap() {{
                    put("id", currentOrder);
                    put("status", 2);
                    put("realStart", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                }};
                videoDiagnoseDao.updateStatus(param);

                //向医生端发送WebSocket消息，告知问诊开始
                WebSocketService.sendInfo("StartDiagnose" + "#" + roomId + "&" + currentOrder, userId + "");
            }

            //问诊结束缓存过期
            else if (key.startsWith("video_diagnose_end_")) {
                //更新上线缓存中的当前问诊状态
                redisTemplate.opsForHash().put(onlineKey, "currentStatus", 3);
                String roomId = redisTemplate.opsForHash().get(onlineKey, "roomId").toString();

                //更新数据库记录
                HashMap param = new HashMap() {{
                    put("id", currentOrder);
                    put("status", 3);
                    put("realEnd", new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
                }};
                videoDiagnoseDao.updateStatus(param);

                Map entries = redisTemplate.opsForHash().entries(onlineKey);
                String nextPatient = MapUtil.getStr(entries, "nextPatient");
                String nextOrder = MapUtil.getStr(entries, "nextOrder");
                String nextStart = MapUtil.getStr(entries, "nextStart");
                String nextEnd = MapUtil.getStr(entries, "nextEnd");
                boolean nextPayment = MapUtil.getBool(entries, "nextPayment");

                //判断等候的问诊订单是否存在
                if (!"none".equals(nextOrder)) {
                    //把等候的问诊换成当前问诊
                    entries.replace("currentPatient", nextPatient);
                    entries.replace("currentOrder", nextOrder);
                    entries.replace("currentHandle", false);
                    entries.replace("currentStart", nextStart);
                    entries.replace("currentEnd", nextEnd);
                    entries.replace("currentPayment", nextPayment);
                    entries.replace("currentNotify", false);
                    entries.replace("currentStatus", 1);

                    //清理等候问诊的缓存
                    entries.replace("nextPatient", "none");
                    entries.replace("nextOrder", "none");
                    entries.replace("nextStart", null);
                    entries.replace("nextEnd", null);
                    entries.replace("nextPayment", false);
                    entries.replace("nextNotify", false);

                } else {
                    entries.replace("currentPatient", "none");
                    entries.replace("currentOrder", "none");
                    entries.replace("currentHandle", false);
                    entries.replace("currentStart", null);
                    entries.replace("currentEnd", null);
                    entries.replace("currentPayment", false);
                    entries.replace("currentNotify", false);
                    entries.replace("currentStatus", null);
                }
                //更新缓存数据
                redisTemplate.opsForHash().putAll(onlineKey, entries);

                //向医生端发送消息，告知问诊结束
                WebSocketService.sendInfo("EndDiagnose" + "#" + roomId, userId + "");
                try {
                    /*
                     * 医生端的JS代码收到推送消息后，会退出TRTC房间。
                     * 所以这里要等待JS退出房间后，销毁该房间。
                     */
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    log.error("线程休眠异常", e);
                }
                //销毁视频问诊Room
                Credential cred = new Credential(cloudSecretId, cloudSecretKey);
                TrtcClient client = new TrtcClient(cred, "ap-beijing");
                DismissRoomByStrRoomIdRequest request = new DismissRoomByStrRoomIdRequest();
                request.setSdkAppId(trtcAppId);
                request.setRoomId(roomId);
                try {
                    client.DismissRoomByStrRoomId(request);
                } catch (TencentCloudSDKException e) {
                    log.error("销毁TRTC房间失败", e);
                }
                redisTemplate.opsForHash().put(key, "roomId", null);
            }

        }//视频问诊挂号缓存被销毁
        else if (key.startsWith("patient_video_diagnose_payment")) {
            //获得挂号单ID
            int id = Integer.parseInt(key.split("#")[1]);
            //查询付款状态
            HashMap map = videoDiagnoseDao.searchPaymentStatus(id);
            int paymentStatus = MapUtil.getInt(map, "paymentStatus");

            int doctorId = MapUtil.getInt(map, "doctorId");
            key = "online_doctor_" + doctorId;

            //只要不是付款成功，就销毁该患者候诊缓存
            if (paymentStatus != 2) {
                Map entries = redisTemplate.opsForHash().entries(key);
                //判断候诊缓存是不是当前的挂号单
                if (id == MapUtil.getInt(entries, "nextOrder")) {
                    redisTemplate.opsForHash().putAll(key, new HashMap() {{
                        put("nextPatient", "none");   //候诊患者Id
                        put("nextOrder", "none");  //等候问诊的订单号
                        put("nextStart", "none");  //等候问诊的开始时间
                        put("nextEnd", "none");    //等候问诊的结束时间
                        put("nextPayment", false); //等候问诊是否付款
                        put("nextNotify", false); //是否通知了医生端
                    }});
                }
                //关闭视频问诊订单（3状态）
                videoDiagnoseDao.closePayment(new HashMap() {{
                    put("id", id);
                }});
            }
            else{

                //使用websocket 通知视频问诊页面刷新
                int userId = misUserDao.searchUserId(new HashMap() {{
                    put("refId", doctorId);
                    put("job", "医生");
                }});
                //推送的消息格式为“消息本体#参数”，这里我只设置了消息本体为RefreshDiagnose
                WebSocketService.sendInfo("RefreshDiagnose", userId + "");
            }

        }
    }
}
