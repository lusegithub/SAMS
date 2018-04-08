package com.jacky.sams.util;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Component
public class ScheduledUtil {
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private ScheduledFuture<?> future;

    private int taskSchedulerCorePoolSize=50;

    static boolean isinitialized=false;

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        threadPoolTaskScheduler=new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(taskSchedulerCorePoolSize);
        threadPoolTaskScheduler.initialize();
        isinitialized=true;
        return threadPoolTaskScheduler;
    }

    private String getCrons(Date date) {
        String dateFormat="ss mm HH dd MM ? yyyy";
        return formatDateByPattern(date, dateFormat);
    }

    private static String formatDateByPattern(Date date, String dateFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formatTimeStr = null;
        if (date != null) {
            formatTimeStr = sdf.format(date);
        }
        return formatTimeStr;
    }


    private ScheduledFuture<?> threadScheduler(Runnable runnable, String cron){
        return future = threadPoolTaskScheduler.schedule(runnable, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        });
    }

    public void addJob(Date date,Runnable runnable){
        String crons=getCrons(date);
        //spring定时任务不支持设置到年份
        threadScheduler(runnable,crons.substring(0,crons.length()-5));
    }
}
