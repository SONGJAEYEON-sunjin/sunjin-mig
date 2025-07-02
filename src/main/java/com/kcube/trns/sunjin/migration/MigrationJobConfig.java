package com.kcube.trns.sunjin.migration;

import com.kcube.trns.sunjin.cache.apitem.ApItemCacheTasklet;
import com.kcube.trns.sunjin.cache.docitem.DocItemCacheTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MigrationJobConfig {

    private final DocItemCacheTasklet docItemCacheTasklet;
    private final ApItemCacheTasklet apItemCacheTasklet;

    @Bean
    public Job parallelMigrationJob(JobRepository jobRepository,
                                    Step parallelDocFlowStep,
                                    Step updateOrgIdByQueryStep,
                                    Step apItemCacheStep,
                                    Step docPartitionedStep,
                                    Step docItemCacheStep,
                                    Step docMigrationStep,
                                    Step docScrtPartitionStep,
                                    Step docLinePartitionStep,
                                    Step docOpnStep,
                                    Step updateOpnGidStep,
                                    Step updateOpnSortStep,
                                    Step docCirDocStep,
                                    Step docForwardStep,
                                    Step docCarbonPartitionStep,
                                    Step docRcrvStep,
                                    Step toDocItemStep,
                                    Step toDocUpdateOrgIdByQueryStep,
                                    Step docSrchPartitionStep,
                                    Step docCarbonStep,
                                    Step docScrtStep,
                                    Step docSrchStep,
                                    Step docLineStep,
                                    Step disableFkStep, Step enableFkStep, Step docFileStep, Step docRfrnStep) {
        return new JobBuilder("parallelMigrationJob", jobRepository)

                 // AP_ITEM
                .start(disableFkStep)
                .next(docMigrationStep)
                .next(updateOrgIdByQueryStep)
                .next(apItemCacheStep)

                // AP_ITEM_SCRT
                .next(docScrtStep)

                // AP_ITEM_SRCH
                .next(docSrchStep)

               // AP_ITEM_LINE
               .next(docLineStep)

                // AP_ITEM_OPN
               .next(docOpnStep)
               .next(updateOpnGidStep)
               .next(updateOpnSortStep)

                // AP_ITEM_SHARE
                .next(docCirDocStep)
                .next(docForwardStep)
                .next(docCarbonStep)

                // AP_ITEM_RCVR
                .next(docRcrvStep)

                // AP_ITEM TO DOC_ITEM
                .next(toDocItemStep)
                .next(toDocUpdateOrgIdByQueryStep)
                .next(docItemCacheStep)

                // AP_ITEM_RFRN
                .next(docRfrnStep)

                // AP_ITEM_FILE
                .next(docFileStep)

                .build();
    }

    @Bean
    public Step parallelDocFlowStep(JobRepository jobRepository,
                                    Flow parallelDocFlow) {
        return new StepBuilder("parallelDocFlowStep", jobRepository)
                .flow(parallelDocFlow)
                .build();
    }

    @Bean
    public Flow parallelDocFlow(Step docOpnStep,
                                Step updateOpnGidStep,
                                Step updateOpnSortStep,
                                Step docCirDocStep,
                                Step docForwardStep,
                                Step docRcrvStep,
                                Step toDocItemStep,
                                Step toDocUpdateOrgIdByQueryStep,
                                Step docItemCacheStep,
                                Step docRfrnStep,
                                Step docCarbonPartitionStep,
                                Step docScrtPartitionStep,
                                Step docSrchPartitionStep,
                                Step docLinePartitionStep,
                                Step docFileStep) {
        return new FlowBuilder<Flow>("parallelFlow")
                .split(flowTaskExecutor())
                .add(
                        new FlowBuilder<SimpleFlow>("opnFlow").start(docOpnStep)
                                .next(updateOpnGidStep)
                                .next(updateOpnSortStep)
                                .build(),
                        new FlowBuilder<SimpleFlow>("scrtFlow").start(docScrtPartitionStep).build(),
                        new FlowBuilder<SimpleFlow>("srchFlow").start(docSrchPartitionStep).build(),
                        new FlowBuilder<SimpleFlow>("lineFlow").start(docLinePartitionStep).build(),
                        new FlowBuilder<SimpleFlow>("fileFlow").start(docFileStep).build(),
                        new FlowBuilder<SimpleFlow>("rcvrFlow").start(docRcrvStep).build(),
                        new FlowBuilder<SimpleFlow>("shareFlow")
                                .start(docCirDocStep)
                                .next(docForwardStep)
                                .next(docCarbonPartitionStep)
                                .build(),
                        new FlowBuilder<SimpleFlow>("toDocFlowRfrn")
                                .start(toDocItemStep)
                                .next(toDocUpdateOrgIdByQueryStep)
                                .next(docItemCacheStep)
                                .next(docRfrnStep)
                                .build()
                )
                .build();
    }

    @Bean
    public TaskExecutor flowTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("step-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step apItemCacheStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager) {
        return new StepBuilder("apItemCacheStep",jobRepository)
                .tasklet(apItemCacheTasklet,transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step docItemCacheStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("docItemCacheStep",jobRepository)
                .tasklet(docItemCacheTasklet,transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step disableFkStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              DataSource dataSource) {
        return new StepBuilder("disableFkStep", jobRepository)
                .tasklet((contribution, context) -> {
                    Connection conn = DataSourceUtils.getConnection(dataSource);
                    Statement stmt = null;
                    try {
                        stmt = conn.createStatement();
                        stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
                    } finally {
                        if (stmt != null) stmt.close();
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }


    @Bean
    public Step enableFkStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             DataSource dataSource) {
        return new StepBuilder("enableFkStep", jobRepository)
                .tasklet((contribution, context) -> {
                    Connection conn = DataSourceUtils.getConnection(dataSource);
                    Statement stmt = null;
                    try {
                        stmt = conn.createStatement();
                        stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
                    } finally {
                        if (stmt != null) stmt.close();
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
