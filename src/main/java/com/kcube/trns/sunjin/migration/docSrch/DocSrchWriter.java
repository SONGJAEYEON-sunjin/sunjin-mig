package com.kcube.trns.sunjin.migration.docSrch;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DocSrchWriter{

    @Bean("docSrchBatchWriter")
    public JdbcBatchItemWriter<DocSrchProcessor.DocSrchRow> docSrchBatchWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<DocSrchProcessor.DocSrchRow>()
                .dataSource(dataSource)
                .sql("""
                INSERT INTO ap_item_srch
                  (ITEMID, CONTENT, TENANTID)
                VALUES
                  (:itemId, :content, :tenantId)
            """)
                .beanMapped()
                .build();
    }
}