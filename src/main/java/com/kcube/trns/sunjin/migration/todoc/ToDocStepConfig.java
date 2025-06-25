package com.kcube.trns.sunjin.migration.todoc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ToDocStepConfig {

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean
    public Step toDocItemStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JdbcPagingItemReader<ApItem> apItemReader,
                              ApItemProcessor apItemProcessor,
                              JdbcBatchItemWriter<MapSqlParameterSource> apItemBatchWriter) {

        return new StepBuilder("toDocItemStep", jobRepository)
                .<ApItem, MapSqlParameterSource>chunk(4000, transactionManager)
                .reader(apItemReader)
                .processor(apItemProcessor)
                .writer(apItemBatchWriter)
                .build();
    }

    @Bean
    public Step toDocUpdateOrgIdByQueryStep(JobRepository jobRepository,
                                            PlatformTransactionManager transactionManager,
                                            DataSource dataSource) {
        return new StepBuilder("toDocUpdateOrgIdByQueryStep", jobRepository)
                .tasklet((contribution, context) -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

                    long chunkSize = 3000; // 한 번에 처리할 row 수
                    long minId = tobeMinItemid;
                    long maxId = tobeMaxItemid;

                    int totalUpdated = 0;

                    for (long start = minId; start <= maxId; start += chunkSize) {
                        long end = start + chunkSize - 1;

                        int updated = jdbcTemplate.update("""
                             UPDATE doc_item target
                            JOIN doc_item source
                              ON target.orgid = source.apprid
                            SET target.orgid = source.itemid
                            WHERE target.trns_src = 'TRNS_SUNJIN_APPR'
                              AND target.orgid IS NOT NULL
                            and target.itemid BETWEEN ? AND ?
                    """, start, end);

                        totalUpdated += updated;
                        log.info("🔄 Updated [{} ~ {}] → {} rows", start, end, updated);
                    }

                    log.info("✅ Total updated rows: " + totalUpdated);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
