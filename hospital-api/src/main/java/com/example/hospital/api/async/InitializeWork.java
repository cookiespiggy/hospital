package com.example.hospital.api.async;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.map.MapUtil;
import com.example.hospital.api.quartz.CloseVideoDiagnoseJob;
import com.example.hospital.api.quartz.QuartzUtil;
import com.example.hospital.api.quartz.VideoDiagnoseJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@Service
public class InitializeWork {
    @Resource
    private QuartzUtil quartzUtil;


    @Resource
    private RedisTemplate redisTemplate;

    @Async("AsyncTaskExecutor")
    public void init() {
        //这里是新添加的代码。遍历Redis中医生上线缓存，清空已经过期的视频问诊
        Set<String> keys = redisTemplate.keys("online_doctor_*");
        keys.forEach(key -> {
            int doctorId = Integer.parseInt(key.substring(key.lastIndexOf("_") + 1));
            Map<String, Object> entries = redisTemplate.opsForHash().entries(key);
            String currentEnd = MapUtil.getStr(entries, "currentEnd");
            String nextEnd = MapUtil.getStr(entries, "nextEnd");
            DateTime now = new DateTime();
            //默认启动服务器之后，所有医生都禁止视频问诊挂号，除非医生手动开放挂号。
            entries.replace("open", false);
            if (currentEnd != null && !"none".equals(currentEnd) && now.isAfter(new DateTime(currentEnd))) {
                //清除当前问诊
                entries.replace("currentPatient", "none");
                entries.replace("currentOrder", "none");
                entries.replace("currentHandle", false);
                entries.replace("currentStart",  "none");
                entries.replace("currentEnd",  "none");
                entries.replace("currentPayment", false);
                entries.replace("currentNotify", false);
                entries.replace("currentStatus", 1);
            }
            if (nextEnd != null && !"none".equals(nextEnd) && now.isAfter(new DateTime(nextEnd))) {
                //清除等候问诊
                entries.replace("nextPatient", "none");
                entries.replace("nextOrder", "none");
                entries.replace("nextStart",  "none");
                entries.replace("nextEnd",  "none");
                entries.replace("nextPayment", false);
                entries.replace("nextNotify", false);
            }
        });



        //每隔3秒钟执行一遍定时器
        String cron = "*/3 * * * * ?";
        JobDetail jobDetail = JobBuilder.newJob(VideoDiagnoseJob.class).build();
        quartzUtil.addJob(jobDetail, "视频问诊定时器", "任务组", cron);

        //TODO 关闭状态不正确的视频问诊挂号单

        //每隔15分钟关闭状态不正确的视频问诊挂号单
        cron = "0 */15 * * * ?";
        jobDetail = JobBuilder.newJob(CloseVideoDiagnoseJob.class).build();
        quartzUtil.addJob(jobDetail, "关闭异常视频问诊状态的定时器", "任务组", cron);
    }

}
