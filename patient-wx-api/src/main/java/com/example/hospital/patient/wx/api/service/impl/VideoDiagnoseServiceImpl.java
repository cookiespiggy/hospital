package com.example.hospital.patient.wx.api.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.example.hospital.patient.wx.api.common.PageUtils;
import com.example.hospital.patient.wx.api.db.dao.UserDao;
import com.example.hospital.patient.wx.api.db.dao.VideoDiagnoseDao;
import com.example.hospital.patient.wx.api.db.dao.VideoDiagnoseFileDao;
import com.example.hospital.patient.wx.api.db.pojo.VideoDiagnoseEntity;
import com.example.hospital.patient.wx.api.db.pojo.VideoDiagnoseFileEntity;
import com.example.hospital.patient.wx.api.exception.HospitalException;
import com.example.hospital.patient.wx.api.service.PaymentService;
import com.example.hospital.patient.wx.api.service.VideoDiagnoseService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VideoDiagnoseServiceImpl implements VideoDiagnoseService {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserDao userDao;

    @Resource
    private VideoDiagnoseDao videoDiagnoseDao;

    @Resource
    private PaymentService paymentService;

    private String notifyUrl = "/video_diagnose/transactionCallback";

    @Resource
    private VideoDiagnoseFileDao videoDiagnoseFileDao;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    private MinioClient client;

    @PostConstruct
    public void init() {
        this.client = new MinioClient.Builder().endpoint(endpoint)
                .credentials(accessKey, secretKey).build();
    }

    @Override
    public ArrayList<HashMap> searchOnlineDoctorList(String subName, String job) {
        ArrayList<HashMap> list = new ArrayList<>();
        //查找缓存中的医生
        Set<String> keys = redisTemplate.keys("online_doctor_*");
        for (String key : keys) {
            Map entries = redisTemplate.opsForHash().entries(key);
            String tempSubName = MapUtil.getStr(entries, "subName");
            String tempJob = MapUtil.getStr(entries, "job");
            boolean open = MapUtil.getBool(entries, "open");
            if (!open) {
                continue;
            }
            //过滤掉不是规定诊室的医生
            if (subName != null && !subName.equals(tempSubName)) {
                continue;
            }
            //过滤掉不是规定职称的医生
            if (job != null && !job.equals(tempJob)) {
                continue;
            }
            HashMap map = new HashMap() {{
                put("doctorId", MapUtil.getInt(entries, "doctorId"));
                put("name", MapUtil.getStr(entries, "name"));
                put("photo", MapUtil.getStr(entries, "photo"));
                put("job", MapUtil.getStr(entries, "job"));
                put("description", MapUtil.getStr(entries, "description"));
                put("remark", MapUtil.getStr(entries, "remark"));
                put("price", MapUtil.getStr(entries, "price"));
            }};
            list.add(map);
        }
        return list;
    }

    @Override
    public HashMap createVideoDiagnose(int userId, VideoDiagnoseEntity entity) {
        HashMap result = new HashMap();

        HashMap map = userDao.searchOpenId(userId);
        String openId = MapUtil.getStr(map, "openId");
        int patientCardId = MapUtil.getInt(map, "patientCardId");
        entity.setPatientCardId(patientCardId);

        String key = "online_doctor_" + entity.getDoctorId();
        //从缓存中获取该医生挂号金额
        String price = redisTemplate.opsForHash().get(key, "price").toString();
        int amount = new BigDecimal(price).multiply(new BigDecimal(100)).intValue();
        entity.setAmount(new BigDecimal(price));

        //尝试用Redis事务挂号
        boolean execute = (Boolean) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //获取医生上线缓存的Version
                operations.watch(key);
                //获取医生上线缓存数据
                Map entries = operations.opsForHash().entries(key);
                boolean open = MapUtil.getBool(entries, "open");
                //如果医生没开启挂号，就不能挂号
                if (!open) {
                    return false;
                }
                String nextPatient = MapUtil.getStr(entries, "nextPatient");
                //如果存在候诊的患者就不能挂号（只能存在一位候诊患者）
                if (!"none".equals(nextPatient)) {
                    return false;
                }
                try {
                    //开启Redis事务
                    operations.multi();
                    //把自己登记为候诊患者
                    operations.opsForHash().put(key, "nextPatient", patientCardId);
                    //提交事务
                    operations.exec();
                    return true;
                } catch (Exception e) {
                    log.debug("事物提交失败", e);
                    return false;
                }
            }
        });

        result.put("flag", execute);
        if (!execute) {
            return result;
        }

        DateTime now = new DateTime();
        //获取医生上线缓存数据
        Map entries = redisTemplate.opsForHash().entries(key);
        //当前问诊患者ID
        String currentPatient = MapUtil.getStr(entries, "currentPatient");

        String expectStart = null;
        String expectEnd = null;
        //如果没有当前问诊患者，定义预计开始和结束时间
        if ("none".equals(currentPatient)) {
            expectStart = now.offsetNew(DateField.MINUTE, 2).toString("yyyy-MM-dd HH:mm:ss");
            expectEnd = now.offsetNew(DateField.MINUTE, 4).toString("yyyy-MM-dd HH:mm:ss");
        }
        //如果有当前问诊患者，就以当前问诊结束时间预估开始和结束时间
        else {
            DateTime currentEnd = new DateTime(MapUtil.getStr(entries, "currentEnd"));
            expectStart = currentEnd.offsetNew(DateField.MINUTE, 2).toString("yyyy-MM-dd HH:mm:ss");
            expectEnd = currentEnd.offsetNew(DateField.MINUTE, 4).toString("yyyy-MM-dd HH:mm:ss");
        }

        entity.setExpectStart(expectStart);
        entity.setExpectEnd(expectEnd);

        String outTradeNo = IdUtil.simpleUUID().toUpperCase();
        //设置支付单过期时间为1分钟。如果超过这个时间不付款，支付单自动关闭
        String timeExpire = now.offsetNew(DateField.MINUTE, 1).toString("yyyy-MM-dd'T'HH:mm:ss'+08:00'");

        //生成微信支付订单
        ObjectNode objectNode = paymentService.unifiedOrder(outTradeNo, openId, amount, "挂号费", notifyUrl, timeExpire);
        String prepayId = objectNode.get("prepay_id").textValue();  //预支付订单ID

        entity.setOutTradeNo(outTradeNo);
        entity.setPrepayId(prepayId);

        //保存视频问诊挂号记录
        videoDiagnoseDao.insert(entity);

        //查询视频问诊记录的主键值
        HashMap data = videoDiagnoseDao.searchByOutTradeNo(outTradeNo);
        int id = MapUtil.getInt(data, "id");

        HashMap cache = new HashMap();
        cache.put("nextOrder", id);
        cache.put("nextPayment", false);
        cache.put("nextStart", expectStart);
        cache.put("nextEnd", expectEnd);
        cache.put("nextNotify", false);
        //更新缓存数据
        redisTemplate.opsForHash().putAll(key, cache);

        //微信付款的相关信息
        result.put("outTradeNo", outTradeNo);
        result.put("prepayId", prepayId);
        result.put("timeStamp", objectNode.get("timeStamp").asText());
        result.put("nonceStr", objectNode.get("nonceStr").asText());
        result.put("package", objectNode.get("package").asText());
        result.put("signType", objectNode.get("signType").asText());
        result.put("paySign", objectNode.get("paySign").asText());
        result.put("videoDiagnoseId", id);
        result.put("expectStart", expectStart);
        result.put("expectEnd", expectEnd);

        //创建付款缓存，1分钟之后销毁
        String paymentKey = "patient_video_diagnose_payment#" + id;
        redisTemplate.opsForValue().set(paymentKey, false);
        //接收付款通知消息和主动查询付款结果都需要短暂的时间，所以缓存过期时间要设置大于60秒
        redisTemplate.expireAt(paymentKey, now.offset(DateField.SECOND, 90));
        return result;

    }

    @Override
    @Transactional
    public void updatePayment(Map param) {
        String outTradeNo = MapUtil.getStr(param, "outTradeNo");
        //根据流水号查询挂号信息
        HashMap data = videoDiagnoseDao.searchByOutTradeNo(outTradeNo);
        int id = MapUtil.getInt(data, "id");
        int doctorId = MapUtil.getInt(data, "doctorId");

        String key = "online_doctor_" + doctorId;
        //获取医生上线缓存
        Map entries = redisTemplate.opsForHash().entries(key);
        //获取当前问诊挂号单ID
        String currentOrder = MapUtil.getStr(entries, "currentOrder");
        //获取排队问诊挂号单ID
        String nextOrder = MapUtil.getStr(entries, "nextOrder");

        //如果挂号单是当前问诊的，就更新上线缓存
        if (currentOrder.equals(id + "")) {
            redisTemplate.opsForHash().put(key, "currentPayment", true);
            redisTemplate.opsForHash().put(key, "currentNotify", false);
        }
        //如果挂号单是排队问诊的，就更新上线缓存
        else if (nextOrder.equals(id + "")) {
            redisTemplate.opsForHash().put(key, "nextPayment", true);
            redisTemplate.opsForHash().put(key, "nextNotify", false);
        }
        //如果挂号单既不属于当前问诊页不属于排队问诊，就执行退款
        else {
            log.error("没有找到缓存，无法跟新付款缓存");
            //TODO 执行退款或者通知运维人员
        }

        //更新数据库中的付款状态
        videoDiagnoseDao.updatePayment(param);

        int paymentStatus = MapUtil.getInt(param, "paymentStatus");
        if (paymentStatus == 2) {
            redisTemplate.expire("patient_video_diagnose_payment#" + id, 5, TimeUnit.SECONDS);
        }
    }


    @Transactional
    @Override
    public boolean searchPaymentResult(String outTradeNo) {
        String transactionId = paymentService.searchPaymentResult(outTradeNo);
        if (transactionId != null) {
            this.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);
                put("transactionId", transactionId);
                put("paymentStatus", 2);
            }});
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArrayList<HashMap> searchImageByVideoDiagnoseId(int videoDiagnoseId) {
        ArrayList<HashMap> list = videoDiagnoseFileDao.searchImageByVideoDiagnoseId(videoDiagnoseId);
        return list;
    }

    @Override
    @Transactional
    public String uploadImage(MultipartFile file, Integer videoDiagnoseId) {
        try {
            //随机生成文件名称
            String filename = IdUtil.simpleUUID() + ".jpg";
            //照片存放在Minio存储桶中的相对路径
            String path = "patient-wx/video_diagnose/" + filename;
            //在Minio中保存照片（文件不能超过5M）
            this.client.putObject(PutObjectArgs.builder().bucket("hospital")
                    .object(path).stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                    .contentType("image/jpeg").build());

            VideoDiagnoseFileEntity entity = new VideoDiagnoseFileEntity();
            entity.setVideoDiagnoseId(videoDiagnoseId);
            entity.setFilename(filename);
            entity.setPath(path);
            //文件属性信息保存到数据表
            videoDiagnoseFileDao.insert(entity);
            return filename;
        } catch (Exception e) {
            log.error("保存上传图片失败", e);
            throw new HospitalException("保存上传图片失败");
        }
    }



    @Override
    @Transactional
    public void deleteImage(Map param) {
        int videoDiagnoseId = MapUtil.getInt(param, "videoDiagnoseId");

        //判断是否包含filename参数
        String filename = MapUtil.getStr(param, "filename");
        //删除某个照片
        if (filename != null) {
            this.removeImageFile(filename);
        }
        //删除该视频问诊所有照片
        else {
            //查询该视频问诊所有照片
            ArrayList<String> list = videoDiagnoseFileDao.searchVideoDiagnoseImage(videoDiagnoseId);
            list.forEach(one -> {
                this.removeImageFile(one);
            });
        }
        //删除数据表记录
        videoDiagnoseFileDao.delete(param);
    }

    @Override
    public String searchRoomId(int doctorId) {
        String key = "online_doctor_" + doctorId;
        Map entries = redisTemplate.opsForHash().entries(key);
        String roomId = MapUtil.getStr(entries, "roomId");
        return roomId;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = videoDiagnoseDao.searchCount(param);
        if (count > 0) {
            list = videoDiagnoseDao.searchByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    private void removeImageFile(String filename) {
        try {
            String path = "patient-wx/video_diagnose/" + filename;
            //删除文件
            this.client.removeObject(RemoveObjectArgs.builder().bucket("hospital")
                    .object(path).build());
        } catch (Exception e) {
            log.error("删除视频问诊图片失败", e);
            throw new HospitalException("删除视频问诊图片失败");
        }
    }
}
