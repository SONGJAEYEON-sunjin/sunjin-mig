package com.kcube.trns.sunjin.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CacheJobConfig {

    private final CacheInitTasklet cacheInitTasklet;

    @Bean
    public Job cacheInitJob(JobRepository jobRepository,
                            Step cacheStep
    ) {
        return new JobBuilder("cacheInitJob",jobRepository)
                .start(cacheStep)
                .build();
    }

    @Bean
    public Step cacheStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("cacheStep",jobRepository)
                .tasklet(cacheInitTasklet,transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
