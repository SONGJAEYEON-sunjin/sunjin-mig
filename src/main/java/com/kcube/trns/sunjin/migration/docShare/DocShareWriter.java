package com.kcube.trns.sunjin.migration.docShare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocShareWriter {

    @Bean("docShareBatchItemWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> docShareWriter(DataSource dataSource) {
        JdbcBatchItemWriter<MapSqlParameterSource> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("""
        INSERT INTO ap_item_share
        (ITEMID, GROUPID, GROUP_NAME, `TYPE`, RGST_DATE)
        VALUES (:ITEMID, :GROUPID, :GROUP_NAME, :TYPE, :RGST_DATE)
    """);
        writer.setItemSqlParameterSourceProvider(item -> item);
        return writer;
    }


}



