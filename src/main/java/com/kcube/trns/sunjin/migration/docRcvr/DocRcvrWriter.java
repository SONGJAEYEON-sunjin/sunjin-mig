package com.kcube.trns.sunjin.migration.docRcvr;

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
public class DocRcvrWriter {

    @Bean("docRcvrBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> docRcvrWriter(DataSource dataSource) {

        JdbcBatchItemWriter<MapSqlParameterSource> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setSql("""
            INSERT INTO ap_item_rcvr
            (ITEMID, ACCTID, ACCT_YN, ACCT_DATE, GROUPID, GROUP_NAME)
            VALUES (:ITEMID, :ACCTID, :ACCT_YN, :ACCT_DATE, :GROUPID, :GROUP_NAME)
        """);
        writer.setItemSqlParameterSourceProvider(item -> item);
        return writer;
    }

}
