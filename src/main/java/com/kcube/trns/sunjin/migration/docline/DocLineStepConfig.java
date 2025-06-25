package com.kcube.trns.sunjin.migration.docline;

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
public class DocLineStepConfig {

    @Value("${migration.asis-min-documentid}")
    private Long asisMinDocumentid;

    @Value("${migration.asis-max-documentid}")
    private Long asisMaxDocumentid;

    @Bean
    public Step docLinePartitionStep(JobRepository jobRepository,
                            Partitioner  docLinePartitioner,
                            Step  docLineSlaveStep,
                            TaskExecutor  docLineTaskExecutor) {
        return new StepBuilder("docLinePartitionStep", jobRepository)
                .partitioner("docLineSlaveStep", docLinePartitioner)
                .step(docLineSlaveStep)
                .gridSize(6)
                .taskExecutor(docLineTaskExecutor)
                .build();
    }

    @Bean
    public Step docLineSlaveStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     JdbcPagingItemReader<DocLineRow> docLinePartitionReader,
                                     DocLineProcessor docLineProcessor,
                                     JdbcBatchItemWriter<MapSqlParameterSource> docLineBatchWriter) {
        return new StepBuilder("docLineSlaveStep", jobRepository)
                .<DocLineRow, MapSqlParameterSource>chunk(5000, transactionManager)
                .reader(docLinePartitionReader)
                .processor(docLineProcessor)
                .writer(docLineBatchWriter)
                .build();
    }

    @Bean
    @StepScope
    public Partitioner docLinePartitioner() {
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
    public TaskExecutor docLineTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step docLineStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcPagingItemReader<DocLineRow> docLinePagingReader,
                            DocLineProcessor docLineProcessor,
                            JdbcBatchItemWriter<MapSqlParameterSource> docLineBatchWriter) {
        return new StepBuilder("docLineStep", jobRepository)
                .<DocLineRow, MapSqlParameterSource>chunk(5000, transactionManager)
                .reader(docLinePagingReader)
                .processor(docLineProcessor)
                .writer(docLineBatchWriter)
                .build();
    }
}
