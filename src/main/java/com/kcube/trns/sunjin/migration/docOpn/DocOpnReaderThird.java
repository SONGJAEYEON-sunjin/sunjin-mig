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
public class DocOpnReaderThird {

    private final DataSource dataSource;

    @Bean("DocOpnReaderThird")
    public JdbcPagingItemReader<DocOpnRow> docOpnReader() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();

        provider.setSelectClause("""
                    a.documentid        AS parentDocumentid,
                    b.documentid        AS childDocumentid,
                    rp.section          AS section,
                    rp.content          AS content,
                    rp.userid           AS userid,
                    rp.NameBase         AS namebase,
                    rp.writetime        AS writetime,
                    rp.ShortReplyID     AS shortReplyId
                """);

        provider.setFromClause("""
                    FROM dp_app_doc a
                    JOIN dp_app_doc b
                      ON b.parentDocumentid = a.documentid
                    JOIN DP_APP_ShortReply rp
                      ON rp.documentid = a.documentid
                """);

        provider.setWhereClause("""
                    WHERE a.ApprovalState = 'C'
                      AND a.AccountTag LIKE 'ZD%'
                """);

        provider.setSortKeys(Map.of(
                "shortReplyId", Order.ASCENDING
        ));

        log.info(">>> DocOpnReaderThird");

        return new JdbcPagingItemReaderBuilder<DocOpnRow>()
                .name("DocOpnReaderThird")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(1000)
                .rowMapper((rs, rowNum) -> new DocOpnRow(
                        rs.getLong("childDocumentid"),
                        rs.getInt("section"),
                        rs.getString("content"),
                        rs.getLong("userid"),
                        rs.getString("namebase"),
                        toLocalDateTimeSafe(rs.getTimestamp("writetime")),
                        rs.getLong("shortReplyId")
                ))
                .build();
    }

    private LocalDateTime toLocalDateTimeSafe(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
