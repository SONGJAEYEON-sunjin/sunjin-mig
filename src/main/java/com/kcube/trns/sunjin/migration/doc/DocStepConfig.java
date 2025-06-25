package com.kcube.trns.sunjin.migration.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocStepConfig {

    @Value("${migration.asis-min-documentid}")
    private Long asisMinDocumentid;

    @Value("${migration.asis-max-documentid}")
    private Long asisMaxDocumentid;

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean
    public Step docPartitionedStep(JobRepository jobRepository,
                                   TaskExecutor docTaskExecutor,
                                   Partitioner  docAllPartitioner,
                                   Step docSlaveStep) {

        return new StepBuilder("docPartitionedStep", jobRepository)
                .partitioner("docSlaveStep", docAllPartitioner)
                .step(docSlaveStep)
                .gridSize(20)
                .taskExecutor(docTaskExecutor)
                .build();
    }

    @Bean
    public TaskExecutor docTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(120);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    @StepScope
    public Partitioner docAllPartitioner() {
        return gridSize -> {
            Long minId = asisMinDocumentid;
            Long maxId = asisMaxDocumentid;

            if (minId == null || maxId == null || minId > maxId) {
                throw new IllegalStateException("유효한 범위를 찾을 수 없습니다.");
            }

            long range = (maxId - minId) / gridSize;
            Map<String, ExecutionContext> result = new HashMap<>();
            long start = minId;

            for (int i = 0; i < gridSize; i++) {
                long end = (i == gridSize - 1) ? maxId : start + range;
                ExecutionContext context = new ExecutionContext();
                context.putLong("minId", start);
                context.putLong("maxId", end);
                result.put("partition" + i, context);
                start = end + 1;
            }

            return result;
        };
    }

    @Bean
    public Step docSlaveStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             JdbcPagingItemReader<DocRow> docRowPartitionReader,
                             DocProcessor docRowProcessor,
                             JdbcBatchItemWriter<MapSqlParameterSource> docRowBatchWriter) {

        return new StepBuilder("docSlaveStep", jobRepository)
                .<DocRow, MapSqlParameterSource>chunk(2000, transactionManager)
                .reader(docRowPartitionReader)
                .processor(docRowProcessor)
                .writer(docRowBatchWriter)
                .build();
    }

    @Bean
    public Step docMigrationStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 JdbcPagingItemReader<DocRow> docRowReader,
                                 DocProcessor docRowProcessor,
                                 JdbcBatchItemWriter<MapSqlParameterSource> docRowBatchWriter) {

        return new StepBuilder("docMigrationStep", jobRepository)
                .<DocRow, MapSqlParameterSource>chunk(2000, transactionManager)
                .reader(docRowReader)
                .processor(docRowProcessor)
                .writer(docRowBatchWriter)
                .build();
    }

    @Bean
    public Step updateOrgIdByQueryStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       DataSource dataSource) {
        return new StepBuilder("updateOrgIdByQueryStep", jobRepository)
                .tasklet((contribution, context) -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

                    long chunkSize = 5000;
                    long minId = tobeMinItemid;
                    long maxId = tobeMaxItemid;

                    int totalUpdated = 0;

                    for (long start = minId; start <= maxId; start += chunkSize) {
                        long end = start + chunkSize - 1;

                        int updated = jdbcTemplate.update("""
                            UPDATE ap_item target
                            JOIN ap_item source
                            ON target.orgid = source.trns_key
                            SET target.orgid = source.itemid
                            WHERE target.trns_src = 'TRNS_SUNJIN_APPR'
                            AND target.orgid IS NOT NULL
                            and target.itemid BETWEEN ? AND ?
                    """, start, end);

                        totalUpdated += updated;
                        log.info(">>> Updated [{} ~ {}] → {} rows", start, end, updated);
                    }

                    log.info(">>> Total updated rows: " + totalUpdated);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
