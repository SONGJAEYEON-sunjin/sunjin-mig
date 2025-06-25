package com.kcube.trns.sunjin.migration.docRcvr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocRcvrStepConfig {

    @Bean
    public Step docRcrvStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            JdbcPagingItemReader<DocRcvrRow> docRcvrPagingReader,
                            DocRcvrProcessor docRcvrProcessor,
                            JdbcBatchItemWriter<MapSqlParameterSource> docRcvrBatchWriter) {

        return new StepBuilder("docRcrvStep", jobRepository)
                .<DocRcvrRow, MapSqlParameterSource>chunk(1000, transactionManager)
                .reader(docRcvrPagingReader)
                .processor(docRcvrProcessor)
                .writer(docRcvrBatchWriter)
                .build();
    }

    @Bean
    public Step updateRcvrItemIdStep(JdbcTemplate jdbcTemplate, JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("updateRcvrItemIdStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update("""
                UPDATE ap_item_rcvr s
                JOIN ap_item ai ON ai.trns_key = s.ITEMID
                SET s.ITEMID = ai.itemid
                WHERE ai.trns_src = 'TRNS_SUNJIN_APPR'
            """);
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    public Step updateRcvrAcctIdStep(JdbcTemplate jdbcTemplate, JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("updateRcvrAcctIdStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    jdbcTemplate.update("""
                UPDATE ap_item_rcvr s
                JOIN ap_item ai ON ai.trns_key = s.ACCTID
                SET s.ACCTID = ai.itemid
                WHERE ai.trns_src = 'TRNS_SUNJIN_APPR'
            """);
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }
}
