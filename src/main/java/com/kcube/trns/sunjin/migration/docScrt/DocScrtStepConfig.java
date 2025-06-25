package com.kcube.trns.sunjin.migration.docScrt;

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
public class DocScrtStepConfig {

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean
    public Step docScrtPartitionStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     Partitioner docScrtPartitioner,
                                     Step  docScrtSlaveStep,
                                     TaskExecutor docScrtTaskExecutor) {
        return new StepBuilder("docScrtPartitionStep", jobRepository)
                .partitioner("docScrtSlaveStep", docScrtPartitioner)
                .step(docScrtSlaveStep)
                .gridSize(6)
                .taskExecutor(docScrtTaskExecutor)
                .build();
    }

    @Bean
    @StepScope
    public Partitioner docScrtPartitioner() {
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
    public TaskExecutor docScrtTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(6);
        executor.setMaxPoolSize(6);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("partition-thread-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Step docScrtSlaveStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcPagingItemReader<DocScrtReader.ApItem> docScrtPagingPartitionReader,
            ItemProcessor<DocScrtReader.ApItem, DocScrtProcessor.DocScrtRow> docScrtProcessor,
            JdbcBatchItemWriter<DocScrtProcessor.DocScrtRow> docScrtBatchWriter
    ) {
        return new StepBuilder("docScrtSlaveStep", jobRepository)
                .<DocScrtReader.ApItem, DocScrtProcessor.DocScrtRow>chunk(5000,transactionManager)
                .reader(docScrtPagingPartitionReader)
                .processor(docScrtProcessor)
                .writer(docScrtBatchWriter)
//                .faultTolerant()
                .build();
    }

    @Bean
    public Step docScrtStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            JdbcPagingItemReader<DocScrtReader.ApItem> docScrtPagingReader,
            ItemProcessor<DocScrtReader.ApItem, DocScrtProcessor.DocScrtRow> docScrtProcessor,
            JdbcBatchItemWriter<DocScrtProcessor.DocScrtRow> docScrtBatchWriter
    ) {
        return new StepBuilder("docScrtStep", jobRepository)
                .<DocScrtReader.ApItem, DocScrtProcessor.DocScrtRow>chunk(5000,transactionManager)
                .reader(docScrtPagingReader)
                .processor(docScrtProcessor)
                .writer(docScrtBatchWriter)
                .build();
    }



}
