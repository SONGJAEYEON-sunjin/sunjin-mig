package com.kcube.trns.sunjin.migration.docShare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocShareStepConfig {

    @Value("${migration.asis-min-documentid}")
    private Long asisMinDocumentid;

    @Value("${migration.asis-max-documentid}")
    private Long asisMaxDocumentid;

    @Bean
    public Step docCirDocStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JdbcPagingItemReader<DocShareRow> docCirDocPagingReader,
                              DocCirDocProcessor docCirDocProcessor,
                              JdbcBatchItemWriter<MapSqlParameterSource> docShareBatchItemWriter
    ) {

        return new StepBuilder("docCirDocStep", jobRepository)
                .<DocShareRow, MapSqlParameterSource>chunk(800, transactionManager)
                .reader(docCirDocPagingReader)
                .processor(docCirDocProcessor)
                .writer(docShareBatchItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .build();
    }

    @Bean
    public Step docForwardStep(JobRepository jobRepository,
                               PlatformTransactionManager transactionManager,
                               JdbcPagingItemReader<DocShareRow> docForwardPagingReader,
                               DocForwardProcessor docForwardProcessor,
                               JdbcBatchItemWriter<MapSqlParameterSource> docShareBatchItemWriter) {

        return new StepBuilder("docForwardStep", jobRepository)
                .<DocShareRow, MapSqlParameterSource>chunk(1000, transactionManager)
                .reader(docForwardPagingReader)
                .processor(docForwardProcessor)
                .writer(docShareBatchItemWriter)
                .build();
    }

    @Bean
    public Step docCarbonStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JdbcPagingItemReader<DocShareRow> docCarbonPagingReader,
                              DocCarbonProcessor docCarbonProcessor,
                              JdbcBatchItemWriter<MapSqlParameterSource> docShareBatchItemWriter,
                              SkipListener<Object, Object> skipLogger) {

        return new StepBuilder("docCarbonStep", jobRepository)
                .<DocShareRow, MapSqlParameterSource>chunk(2000, transactionManager)
                .reader(docCarbonPagingReader)
                .processor(docCarbonProcessor)
                .writer(docShareBatchItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .listener(skipLogger)
                .build();
    }

    @Bean
    public Step docCarbonPartitionStep(JobRepository jobRepository,
                                     Partitioner  docSharePartitioner,
                                     Step  docCarbonSlaveStep,
                                     TaskExecutor  docShareTaskExecutor) {
        return new StepBuilder("docCarbonPartitionStep", jobRepository)
                .partitioner("docCarbonSlaveStep", docSharePartitioner)
                .step(docCarbonSlaveStep)
                .gridSize(6)
                .taskExecutor(docShareTaskExecutor)
                .build();
    }

    @Bean
    public Step docCarbonSlaveStep(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       JdbcPagingItemReader<DocShareRow> docCarbonPartitionReader,
                                       DocCarbonProcessor docCarbonProcessor,
                                       JdbcBatchItemWriter<MapSqlParameterSource> docShareBatchItemWriter
    ) {
        return new StepBuilder("docCarbonSlaveStep", jobRepository)
                .<DocShareRow, MapSqlParameterSource>chunk(5000, transactionManager)
                .reader(docCarbonPartitionReader)
                .processor(docCarbonProcessor)
                .writer(docShareBatchItemWriter)
                .faultTolerant()
                .build();
    }

    @Bean
    @StepScope
    public Partitioner docSharePartitioner() {
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
    public TaskExecutor docShareTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }
}
