package com.kcube.trns.sunjin.migration.docRfrn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocRfrnReader {

    @Bean("docRfrnPagingReader")
    public JdbcPagingItemReader<DocRfrnRow> apItemRfrnReader(DataSource dataSource) {
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("""
            bd.BaseDocumentID,
            bd.DocumentID AS itemId,
            bd.ParentDocumentID AS rfrnItemId,
            CONCAT(IFNULL(CONCAT('[', d.docnumber, ']'), ''), d.subject) AS title
        """);
        queryProvider.setFromClause("""
                FROM dp_app_basedoc bd
                JOIN dp_app_doc d
                ON bd.parentdocumentid = d.documentid
                join dp_app_doc d2
                on bd.documentid = d2.documentid
        """);
        queryProvider.setWhereClause("""
            where d.approvalstate = 'C'
            and d2.ApprovalState ='C'
        """);
        queryProvider.setSortKeys(Map.of(
                "bd.BaseDocumentID", Order.ASCENDING
        ));
        return new JdbcPagingItemReaderBuilder<DocRfrnRow>()
                .name("apItemRfrnReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) -> new DocRfrnRow(
                        rs.getLong("itemId"),
                        rs.getLong("rfrnItemId"),
                        rs.getString("title")
                ))
                .build();
    }
}
