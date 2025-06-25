package com.kcube.trns.sunjin.migration.docRcvr;

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
public class DocRcvrReader {

    @Bean("docRcvrPagingReader")
    public JdbcPagingItemReader<DocRcvrRow> docRcvrReader(DataSource dataSource) {
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("""
        u.documentid AS documentid,
        CASE WHEN u.ApprovalType = 'S' THEN u.ApprovedDocumentID ELSE NULL END AS acctId,
        'Y' AS acctYn,
        COALESCE(u.ApprovalDate, d.WriteTime) AS acctDate,
        COALESCE(m.userid, u.userid) AS real_userId,
        u.userid,
        u.NameBase,
        u.DeptID,
        u.sequence
    """);
        queryProvider.setFromClause("""
        DP_APP_CounterPartUser u
        LEFT JOIN (
            SELECT m1.*
            FROM DP_APP_CounterPartUserModify m1
            JOIN (
                SELECT documentid, ApprovalTag, MAX(modifySeq) AS maxSeq
                FROM DP_APP_CounterPartUserModify
                GROUP BY documentid, ApprovalTag
            ) m2
              ON m1.documentid = m2.documentid
             AND m1.ApprovalTag = m2.ApprovalTag
             AND m1.modifySeq = m2.maxSeq
        ) m ON u.documentid = m.documentid AND u.ApprovalTag = m.ApprovalTag
        JOIN dp_app_doc d ON u.documentid = d.documentid
    """);
        queryProvider.setWhereClause("""
        u.userid > 0
        AND SUBSTRING(u.ApprovalTag, 1, 2) = 'ZP'
        AND d.approvalstate = 'C'
    """);
        queryProvider.setSortKeys(Map.of(
                "u.DocumentID", Order.ASCENDING,
                "u.userid", Order.ASCENDING,
                "u.deptid", Order.ASCENDING,
                "u.sequence", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocRcvrRow>()
                .name("docRcvrReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .pageSize(1000)
                .rowMapper((rs, rowNum) -> new DocRcvrRow(
                        rs.getLong("documentid"),
                        rs.getLong("acctId"),
                        rs.getString("acctYn"),
                        rs.getTimestamp("acctDate").toLocalDateTime(),
                        rs.getLong("real_userId"),
                        rs.getString("nameBase")
                ))
                .build();
    }
}
