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
                                    Step disableFkStep, Step enableFkStep) {
        return new JobBuilder("parallelMigrationJob", jobRepository)

//                // --- [[ AP_ITEM 이관 ]]
//                .start(docPartitionedStep)
//                .next(updateOrgIdByQueryStep)
//
//                // --- [[ AP_ITEM_XXX 이관 ]]
//                .start(apItemCacheStep)
//                .next(parallelDocFlowStep)

                /**
                 * doc_item_XXX 는 이관프로그램이 아닌 스크립트로 직접 실행(속도이슈)
                 * 스크립트 위치 : /docs/작업템플릿(doc).sql
                 */
                // --- [[  실패복구 STEP  ]] -> 실패한 STEP만 선택적으로 주석해제 후 실행

////                // AP_ITEM
////                .start(docMigrationStep) // DocReader.docRowReader 에서 범위 설정 후 실행
////               .next(updateOrgIdByQueryStep)
//                .start(apItemCacheStep)
//
////                // AP_ITEM_SCRT
////                .start(docScrtStep) // DocScrtReader.docScrtPagingReader 에서 범위 설정 후 실행
////
////                // AP_ITEM_SRCH
////                .start(docSrchStep) // DocSrchReader.docSrchPagingReader 에서 범위 설정 후 실행
////
////               // AP_ITEM_LINE
////               .start(disableFkStep)
////               .next(docLineStep) // DocLineReader.docLineReader 에서 범위 설정 후 실행
////
////
////                // AP_ITEM_OPN
              .start(disableFkStep)
              .next(docOpnStep)            // DocOpnReader.docOpnReader 에서 범위 설정 후 실행
////                .next(updateOpnGidStep)
////                .next(updateOpnSortStep)
////                .next(enableFkStep)
////
////                // AP_ITEM_SHARE
////                .start(docCirDocStep)       // DocShareReader.docCarbonReader 에서 범위 설정 후 실행
////                .next(docForwardStep)       // DocShareReader.docForwardReader 에서 범위 설정 후 실행
////                .next(docCarbonStep)        // DocShareReader.docCarbonReader 에서 범위 설정 후 실행
////
////                // AP_ITEM_RCVR
////                .start(docRcrvStep)     // DocRcvrReader.docRcvrReader 에서 범위 설정 후 실행
////
////                // AP_ITEM TO DOC_ITEM
////                 .start(toDocItemStep) // DocReader.apItemReader 에서 범위 설정 후 실행
////                 .start(toDocUpdateOrgIdByQueryStep)
////                .start(docItemCacheStep)
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
                        new FlowBuilder<SimpleFlow>("shareFlow")
                                .start(docCirDocStep)
                                .next(docForwardStep)
                                .next(docCarbonPartitionStep)
                                .build(),
                        new FlowBuilder<SimpleFlow>("toDocFlow")
                                .start(toDocItemStep)
                                .next(toDocUpdateOrgIdByQueryStep)
                                .next(docItemCacheStep)
                                .build(),
                        new FlowBuilder<SimpleFlow>("rcvrFlow").start(docRcrvStep).build()
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
