package com.kcube.trns.sunjin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JobConfig {

    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final Job cacheInitJob;
    private final Job parallelMigrationJob;

    @Bean
    public Job masterJob() {
        return new JobBuilder("masterJob", jobRepository)
                .start(cacheInitJobStep())
                .next(parallelMigrationJobStep())
                .incrementer(new TimestampParameterIncrementer())
                .build();
    }

    @Bean
    public Step cacheInitJobStep(){
        return new JobStepBuilder(new StepBuilder("cacheInitStep",jobRepository))
                .job(cacheInitJob)
                .launcher(jobLauncher)
                .allowStartIfComplete(true)
                .build();
    }


    @Bean
    public Step parallelMigrationJobStep(){
        return new JobStepBuilder(new StepBuilder("parallelMigrationJobStep",jobRepository))
                .job(parallelMigrationJob)
                .launcher(jobLauncher)
                .allowStartIfComplete(true)
                .build();
    }

    public class TimestampParameterIncrementer implements JobParametersIncrementer {
        @Override
        public JobParameters getNext(JobParameters parameters) {
            return new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
        }
    }
}
