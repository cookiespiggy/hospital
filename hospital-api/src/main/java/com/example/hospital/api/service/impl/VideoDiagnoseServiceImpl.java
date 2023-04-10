package com.example.hospital.api.service.impl;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.db.dao.DoctorDao;
import com.example.hospital.api.db.dao.MisUserDao;
import com.example.hospital.api.db.dao.VideoDiagnoseDao;
import com.example.hospital.api.db.dao.VideoDiagnoseFileDao;
import com.example.hospital.api.exception.HospitalException;
import com.example.hospital.api.service.VideoDiagnoseService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoDiagnoseServiceImpl implements VideoDiagnoseService {
    @Resource
    private DoctorDao doctorDao;

    @Resource
    private MisUserDao misUserDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private VideoDiagnoseDao videoDiagnoseDao;

    @Override
    public void online(int userId) {
        //查找医生的userId查询doctorId。因为经常会有这个操作，所以我定义了封装函数
        int doctorId = this.searchDoctorId(userId);

        String key = "online_doctor_" + doctorId;
        //判断是否存在该医生的上线缓存
        if (redisTemplate.hasKey(key)) {
            return;
        }
        //查询医生详情信息
        HashMap map = doctorDao.searchDataForOnlineCache(doctorId);
        //创建医生上线缓存
        redisTemplate.opsForHash().putAll(key, new HashMap(map) {{
            put("open", false);
            put("currentPatient", "none");   //当前患者Id
            put("currentOrder", "none");  //当前问诊订单号
            put("currentHandle", false);  //当前问诊是否处理了
            put("currentStart", "none");  //开始时间
            put("currentEnd", "none");    //结束时间
            put("currentPayment", false); //当前问诊是否付款
            put("currentStatus", 1);   //1未开始，2问诊中，3已结束
            put("currentNotify", false); //未推送给医生端
            put("nextPatient", "none");   //候诊患者Id
            put("nextOrder", "none");  //等候问诊的订单号
            put("nextStart", "none");  //等候问诊的开始时间
            put("nextEnd", "none");    //等候问诊的结束时间
            put("nextPayment", false); //排队问诊是否付款
            put("nextNotify", false); //未推送给医生端
        }});
    }

    @Override
    public boolean offline(int userId) {
        //查找医生的主键值
        int doctorId = this.searchDoctorId(userId);
        String key = "online_doctor_" + doctorId;
        //判断是否存在上线缓存
        if (!redisTemplate.hasKey(key)) {
            return true;
        }

        //检查是否有正在执行的问诊和等待的问诊
        List<String> list = redisTemplate.opsForHash().multiGet(key, new ArrayList<>() {{
            add("currentOrder");
            add("nextOrder");
        }});
        String currentOrder = list.get(0);
        String nextOrder = list.get(1);

        //存在当前问诊或者排队问诊，医生就不能下线
        if (!"none".equals(currentOrder) || !"none".equals(nextOrder)) {
            return false;
        }

        //删除医生的问诊缓存
        redisTemplate.delete(key);
        return true;
    }

    @Override
    public void updateOpenFlag(int userId, boolean open) {
        //查找医生的主键值
        int doctorId = this.searchDoctorId(userId);
        String key = "online_doctor_" + doctorId;
        //如果不存在上线缓存，医生都没上线，所以就不需要更新open属性值
        if (!redisTemplate.hasKey(key)) {
            return;
        }
        //更新open属性值，开放或者关闭挂号
        redisTemplate.opsForHash().put(key, "open", open);
    }

    @Override
    public HashMap searchVideoDiagnoseInfo(int userId) {
        HashMap map = new HashMap();

        int doctorId = this.searchDoctorId(userId);
        String key = "online_doctor_" + doctorId;
        if (!redisTemplate.hasKey(key)) {
            return map;
        }
        Map entries = redisTemplate.opsForHash().entries(key);
        String currentOrder = MapUtil.getStr(entries, "currentOrder");
        String nextOrder = MapUtil.getStr(entries, "nextOrder");

        Boolean currentPayment = MapUtil.getBool(entries, "currentPayment");
        Boolean nextPayment = MapUtil.getBool(entries, "nextPayment");

        if (!"none".equals(currentOrder) && (currentPayment != null && currentPayment)) {
            int diagnoseId = Integer.parseInt(currentOrder);
            HashMap currentInfo = videoDiagnoseDao.searchVideoDiagnoseInfo(diagnoseId);
            map.put("currentInfo", currentInfo);
        }
        if (!"none".equals(nextOrder) && (nextPayment != null && nextPayment)) {
            int diagnoseId = Integer.parseInt(nextOrder);
            HashMap nextInfo = videoDiagnoseDao.searchVideoDiagnoseInfo(diagnoseId);
            map.put("nextInfo", nextInfo);
        }
        return map;
    }


    @Override
    public HashMap refreshInfo(int userId) {
        int doctorId = this.searchDoctorId(userId);
        String key = "online_doctor_" + doctorId;
        //把医生上线缓存的部分属性数据取出来，保存到这个Map中返回给浏览器
        HashMap map = new HashMap();

        //不存在医生上线缓存的情况
        if (!redisTemplate.hasKey(key)) {
            map.put("status", "offline");
            return map;
        }

        Map entries = redisTemplate.opsForHash().entries(key);
        map.put("status", "online");
        map.put("open", MapUtil.getBool(entries, "open"));

        map.put("currentOrder", MapUtil.getStr(entries, "currentOrder"));
        Integer currentStatus = MapUtil.getInt(entries, "currentStatus");
        map.put("currentStatus", currentStatus);
        //如果患者已经付款会有程序往上线缓存中添加以及视频问诊的RoomId
        if (currentStatus != null && currentStatus == 2) {
            map.put("roomId", MapUtil.getStr(entries, "roomId"));
        }
        map.put("currentStart", MapUtil.getStr(entries, "currentStart"));
        map.put("currentEnd", MapUtil.getStr(entries, "currentEnd"));

        return map;
    }

    @Resource
    private VideoDiagnoseFileDao videoDiagnoseFileDao;

    @Override
    public ArrayList<String> searchImageByVideoDiagnoseId(int videoDiagnoseId) {
        ArrayList<String> list = videoDiagnoseFileDao.searchImageByVideoDiagnoseId(videoDiagnoseId);
        return list;
    }

    @Override
    public HashMap searchMyStatistics(int userId) {
        int doctorId = this.searchDoctorId(userId);
        ArrayList<HashMap> list_1 = videoDiagnoseDao.searchStatisticsOfSevenDays(doctorId);
        ArrayList<HashMap> list_2 = videoDiagnoseDao.searchStatisticsOfYear(doctorId);
        ArrayList tempList = new ArrayList();
        int year = new DateTime().year();
        for (int i = 1; i <= 12; i++) {
            String tempMonth = "";
            if (i < 10) {
                tempMonth = "0" + i;
            } else {
                tempMonth += i;
            }
            String temp = year + "-" + tempMonth;

            boolean flag = false;
            HashMap tempMap = new HashMap();
            long count = 0;
            for (HashMap one : list_2) {
                String month = MapUtil.getStr(one, "month");
                if (month.equals(temp)) {
                    flag = true;
                    tempMap = one;
                    break;
                }
            }
            if (flag == false) {
                tempList.add(new HashMap<>() {{
                    put("month", temp);
                    put("count", 0);
                }});
            } else {
                tempList.add(tempMap);
            }

        }

        HashMap map = new HashMap() {{
            put("days", list_1);
            put("year", tempList);
        }};
        return map;
    }

    private int searchDoctorId(int userId) {
        HashMap map = misUserDao.searchRefId(userId);
        String job = MapUtil.getStr(map, "job");

        if (!"医生".equals(job)) {
            throw new HospitalException("当前用户不是医生");
        }

        Integer refId = MapUtil.getInt(map, "refId");
        if (refId == null) {
            throw new HospitalException("当前用户没有关联医生表");
        }
        return refId;
    }
}

