package com.kcube.trns.sunjin.migration.docScrt;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DocScrtWriter {

    @Bean("docScrtBatchWriter")
    public JdbcBatchItemWriter<DocScrtProcessor.DocScrtRow> docScrtBatchWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<DocScrtProcessor.DocScrtRow>()
                .dataSource(dataSource)
                .sql("""
                INSERT INTO ap_item_scrt
                  (ITEMID, GROUPID, GROUP_NAME)
                VALUES
                  (:itemId, :groupId, :groupName)
            """)
                .beanMapped()
                .build();
    }
}
