package com.kcube.trns.sunjin.migration.docSrch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocSrchStepConfig {

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean
    public Step docSrchPartitionStep(JobRepository jobRepository,
                                     Partitioner docSrchPartitioner,
                                     Step  docSrchSlaveStep,
                                     TaskExecutor docSrchTaskExecutor) {
        return new StepBuilder("docSrchPartitionStep", jobRepository)
                .partitioner("docSrchSlaveStep", docSrchPartitioner)
                .step(docSrchSlaveStep)
                .gridSize(6)
                .taskExecutor(docSrchTaskExecutor)
                .build();
    }

    @Bean
    @StepScope
    public Partitioner docSrchPartitioner() {
        return gridSize -> {
            Long minId = tobeMinItemid;
            Long maxId = tobeMaxItemid;

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
    public TaskExecutor docSrchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step docSrchSlaveStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcPagingItemReader<DocSrchReader.DocSrchDto> docSrchPartitionReader,
            ItemProcessor<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow> docSrchProcessor,
            JdbcBatchItemWriter<DocSrchProcessor.DocSrchRow> docSrchWriter
    ) {
        return new StepBuilder("docSrchSlaveStep", jobRepository)
                .<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow>chunk(5000,transactionManager)
                .reader(docSrchPartitionReader)
                .processor(docSrchProcessor)
                .writer(docSrchWriter)
                .build();
    }

    @Bean
    public Step docSrchStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcPagingItemReader<DocSrchReader.DocSrchDto> docSrchPagingReader,
            ItemProcessor<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow> docSrchProcessor,
            JdbcBatchItemWriter<DocSrchProcessor.DocSrchRow> docSrchWriter
    ) {
        return new StepBuilder("docSrchStep", jobRepository)
                .<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow>chunk(5000,transactionManager)
                .reader(docSrchPagingReader)
                .processor(docSrchProcessor)
                .writer(docSrchWriter)
                .build();
    }
}
