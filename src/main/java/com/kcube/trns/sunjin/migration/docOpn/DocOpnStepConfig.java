package com.kcube.trns.sunjin.migration.docOpn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class DocOpnStepConfig {

    @Value("${migration.asis-min-documentid}")
    private Long asisMinDocumentid;

    @Value("${migration.asis-max-documentid}")
    private Long asisMaxDocumentid;

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean
    public Step docOpnStep(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           @Qualifier("docOpnPagingReader") JdbcPagingItemReader<DocOpnRow> docOpnReader,
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
    public Step docOpnStepFirst(JobRepository jobRepository,
                           PlatformTransactionManager transactionManager,
                           JdbcPagingItemReader<DocOpnRow> DocOpnReaderFirst,
                           DocOpnProcessor docOpnProcessor,
                           JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStepFirst", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(200, transactionManager)
                .reader(DocOpnReaderFirst)
                .processor(docOpnProcessor)
                .writer(docOpnBatchWriter)
                .build();
    }

    @Bean
    public Step docOpnStepSecond(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcPagingItemReader<DocOpnRow> DocOpnReaderSecond,
                                DocOpnProcessor docOpnProcessor,
                                JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStepSecond", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(200, transactionManager)
                .reader(DocOpnReaderSecond)
                .processor(docOpnProcessor)
                .writer(docOpnBatchWriter)
                .build();
    }

    @Bean
    public Step docOpnStepThird(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 JdbcPagingItemReader<DocOpnRow> DocOpnReaderThird,
                                 DocOpnProcessor docOpnProcessor,
                                 JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStepThird", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(1000, transactionManager)
                .reader(DocOpnReaderThird)
                .processor(docOpnProcessor)
                .writer(docOpnBatchWriter)
                .build();
    }

    @Bean
    public Step docOpnStepFourth(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                JdbcPagingItemReader<DocOpnRow> DocOpnReaderFourth,
                                DocOpnProcessor docOpnProcessor,
                                JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStepFourth", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(200, transactionManager)
                .reader(DocOpnReaderFourth)
                .processor(docOpnProcessor)
                .writer(docOpnBatchWriter)
                .build();
    }

    @Bean
    public Step docOpnStepFifth(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 JdbcPagingItemReader<DocOpnRow> DocOpnReaderFifth,
                                 DocOpnProcessor docOpnProcessor,
                                 JdbcBatchItemWriter<MapSqlParameterSource> docOpnBatchWriter) {

        return new StepBuilder("docOpnStepFifth", jobRepository)
                .<DocOpnRow, MapSqlParameterSource>chunk(1000, transactionManager)
                .reader(DocOpnReaderFifth)
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
                        WHERE i.trns_src = 'TRNS_SUNJIN_APPR'
                        and itemid between ? and ? ;
                """,tobeMinItemid,tobeMaxItemid);
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
                            FROM (SELECT * FROM ap_item_opn WHERE itemid BETWEEN ? AND ? ORDER BY itemid, opnid) AS sorted_opn,
                                 (SELECT @sort := 0, @prev_itemid := NULL) vars
                        ) t ON o.opnid = t.opnid
                        SET o.sort = t.new_sort
                        WHERE o.itemid BETWEEN ? AND ?;
                    """, tobeMinItemid, tobeMaxItemid, tobeMinItemid, tobeMaxItemid);

                    log.info("updated rows: {}", updatedCount);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
