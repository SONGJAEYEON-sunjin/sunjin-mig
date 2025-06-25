package com.kcube.trns.sunjin.migration.docOpn;

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

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocOpnStepConfig {

    @Bean
    public Step docOpnStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcPagingItemReader<DocOpnRow> docOpnReader,
                           DocOpnProcessor docOpnProcessor,
                           JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStep", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(3000, transactionManager)
                .reader(docOpnReader)
                .processor(docOpnProcessor)
                .writer(docOpnBatchWriter)
                .build();
    }

    @Bean
    public Step updateOpnItemIdStep(JobRepository jobRepository,
                                    PlatformTransactionManager transactionManager,
                                    DataSource dataSource) {
        return new StepBuilder("updateOpnItemIdStep", jobRepository)
                .tasklet((contribution, context) -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    int updatedCount = jdbcTemplate.update("""
                        UPDATE ap_item_opn o
                        JOIN ap_item i ON o.itemid = i.trns_key
                        SET o.itemid = i.itemid
                        WHERE i.trns_src = 'TRNS_SUNJIN_APPR';
                """);
                    log.info("✅ updated rows: " + updatedCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step updateOpnGidStep(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 DataSource dataSource) {
        return new StepBuilder("updateOpnGidStep", jobRepository)
                .tasklet((contribution, context) -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                    int updatedCount = jdbcTemplate.update("""
                         UPDATE ap_item_opn o1
                         SET o1.gid = o1.opnid
                """);
                    log.info("updated rows: " + updatedCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step updateOpnSortStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  DataSource dataSource) {

        return new StepBuilder("updateOpnSortStep", jobRepository)
                .tasklet((contribution, context) -> {
                    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

                    jdbcTemplate.execute("SET @prev_itemid := NULL");
                    jdbcTemplate.execute("SET @sort := 0");

                    int updatedCount = jdbcTemplate.update("""
                    UPDATE ap_item_opn o
                    JOIN (
                        SELECT 
                            opnid,
                            itemid,
                            (@sort := IF(@prev_itemid = itemid, @sort + 1, 0)) AS new_sort,
                            @prev_itemid := itemid
                        FROM ap_item_opn
                        ORDER BY itemid, opnid
                    ) t ON o.opnid = t.opnid
                    SET o.sort = t.new_sort
                """);

                    log.info(" updated rows: {}", updatedCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
