package com.mall.config;

import com.mall.job.OrderCloseJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail jobDetail(){
        return JobBuilder.newJob(OrderCloseJob.class)
                .withIdentity("orderJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trigger(){
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail())
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")
                )
                .build();
    }
}