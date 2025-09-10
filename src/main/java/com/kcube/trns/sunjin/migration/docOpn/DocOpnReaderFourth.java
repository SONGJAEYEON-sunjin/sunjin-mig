package com.kcube.trns.sunjin.migration.docOpn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocOpnReaderFourth {

    private final DataSource dataSource;

    @Bean("DocOpnReaderFourth")
    public JdbcPagingItemReader<DocOpnRow> docOpnReader() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
            rp.documentid          AS documentid,
            rp.section             AS section,
            rp.content             AS content,
            rp.userid              AS userid,
            rp.NameBase            AS namebase,
            rp.writetime           AS writetime,
            rp.ShortReplyID        AS shortReplyId
    """);
    provider.setFromClause("""
        FROM DP_APP_ShortReply rp
        JOIN dp_app_doc d
          ON rp.documentid = d.documentid
    """);
    provider.setWhereClause("""
          where d.ApprovalState = 'C'
          AND d.parentdocumentid <> 0
          AND d.AccountTag LIKE 'ZQ%'
    """);

        provider.setSortKeys(Map.of(
                "rp.documentid", Order.ASCENDING,
                "rp.ShortReplyID", Order.ASCENDING
        ));

        log.info(">>> DocOpnReaderFourth ");

        return new JdbcPagingItemReaderBuilder<DocOpnRow>()
                .name("DocOpnReaderFourth")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(200)
                .rowMapper((rs, rowNum) -> new DocOpnRow(
                        rs.getLong("documentid"),
                        rs.getInt("section"),
                        rs.getString("content"),
                        rs.getLong("userid"),
                        rs.getString("namebase"), // 별칭과 일치
                        toLocalDateTimeSafe(rs.getTimestamp("writetime")),
                        rs.getLong("ShortReplyID")
                ))
                .build();
    }

    private LocalDateTime toLocalDateTimeSafe(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}