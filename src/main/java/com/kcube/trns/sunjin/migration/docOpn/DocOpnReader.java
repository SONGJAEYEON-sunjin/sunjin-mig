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
public class DocOpnReader {

    private final DataSource dataSource;

    @Bean("docOpnPagingReader")
    public JdbcPagingItemReader<DocOpnRow> docOpnReader() {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
            rp.documentid, rp.section, rp.content, rp.userid, rp.NameBase, rp.writetime, rp.ShortReplyID
        """);
        provider.setFromClause("""
            FROM dp_app_shortreply rp
            JOIN ap_item a ON rp.documentid = a.trns_key
        """);
        provider.setWhereClause("""
            WHERE a.trns_src = 'TRNS_SUNJIN_APPR'
           """);
        provider.setSortKeys(Map.of(
                "rp.documentid", Order.ASCENDING,
                "rp.ShortReplyID", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocOpnRow>()
                .name("docOpnReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(3000)
                .rowMapper((rs, rowNum) -> new DocOpnRow(
                        rs.getLong("documentid"),
                        rs.getInt("section"),
                        rs.getString("content"),
                        rs.getLong("userid"),
                        rs.getString("namebase"),
                        toLocalDateTimeSafe(rs.getTimestamp("writetime")),
                        rs.getLong("ShortReplyID")
                ))
                .build();
    }

    private LocalDateTime toLocalDateTimeSafe(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}