package com.example.hospital.api.quartz;

import com.example.hospital.api.db.dao.VideoDiagnoseDao;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Slf4j
public class CloseVideoDiagnoseJob extends QuartzJobBean {
    @Resource
    private VideoDiagnoseDao videoDiagnoseDao;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        videoDiagnoseDao.closeVideoDiagnose();
    }
}

