package com.kcube.trns.sunjin.migration.docRfrn;

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
public class DocRfrnWriter {

    @Bean("docRfrnBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> apItemRfrnWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .sql("""
                INSERT INTO ap_item_rfrn
                (ITEMID, RFRN_ITEMID, TITLE, DIVISION)
                VALUES (:ITEMID, :RFRN_ITEMID, :TITLE, :DIVISION)
            """)
                .itemSqlParameterSourceProvider(item -> item)
                .dataSource(dataSource)
                .build();
    }

}
