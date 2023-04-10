package com.example.hospital.api.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class QuartzUtil {
    @Resource
    private Scheduler scheduler;

    public void addJob(JobDetail jobDetail, String jobName, String jobGroupName, String cron) {
        try {
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
            scheduler.scheduleJob(jobDetail, trigger);
            log.debug("成功添加" + jobName + "定时器");
        } catch (SchedulerException e) {
            log.error("定时器添加失败", e);
        }
    }

    public void deleteJob(String jobName, String jobGroupName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        try {
            scheduler.resumeTrigger(triggerKey);
            scheduler.unscheduleJob(triggerKey);
            scheduler.deleteJob(JobKey.jobKey(jobName, jobGroupName));
            log.debug("成功删除" + jobName + "定时器");
        } catch (SchedulerException e) {
            log.error("定时器删除失败", e);
        }

    }
}

