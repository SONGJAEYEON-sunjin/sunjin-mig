package com.kcube.trns.sunjin.migration.docFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocFileStepConfig {

    @Bean
    public Step docFileStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcPagingItemReader<DocFileRow> docFilePagingReader,
                            DocFileProcessor docFileProcessor,
                            JdbcBatchItemWriter<MapSqlParameterSource> docFileBatchWriter) {

        return new StepBuilder("docFileStep", jobRepository)
                .<DocFileRow, MapSqlParameterSource>chunk(5000, transactionManager)
                .reader(docFilePagingReader)
                .processor(docFileProcessor)
                .writer(docFileBatchWriter)
                .build();
    }
}
