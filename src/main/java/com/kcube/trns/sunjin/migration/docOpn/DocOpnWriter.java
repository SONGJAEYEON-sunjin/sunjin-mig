package com.kcube.trns.sunjin.migration.docOpn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocOpnWriter {

    private final DataSource dataSource;

    @Bean("docOpnBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> docOpnWriter() {
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .dataSource(dataSource)
                .sql("""
                INSERT INTO ap_item_opn (
                    ITEMID, GID, TYPE, CONTENT, SORT, RATE,
                    USERID, USER_NAME, USER_DISP, RGST_DATE, LAST_DATE, IMMUTABLE
                ) VALUES (
                    :ITEMID, :GID, :TYPE, :CONTENT, :SORT, :RATE,
                    :USERID, :USER_NAME, :USER_DISP, :RGST_DATE, :LAST_DATE, :IMMUTABLE
                )
            """).itemSqlParameterSourceProvider(item -> item)
                .build();
    }
}