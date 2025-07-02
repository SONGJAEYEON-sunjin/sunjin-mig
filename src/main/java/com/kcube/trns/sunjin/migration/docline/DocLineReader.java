package com.kcube.trns.sunjin.migration.docline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocLineReader {

    private final DataSource dataSource;

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean("docLinePartitionReader")
    @StepScope
    public JdbcPagingItemReader<DocLineRow> docLinePartitionReader(
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {

        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
                seq.DocumentID,
                seq.sequence,
                seq.userId,
                seq.nameBase,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN NULL
                    ELSE seq.ViewingTime
                END AS ViewingTime,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN NULL
                    ELSE seq.ApprovalDate
                END AS ApprovalDate,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN "READY"
                    ELSE seq.ApprovalType  
                END AS ApprovalType, 
                seq.ApprovalTag,
                seq.Proxy,
                seq.ProxyUserID,
                seq.proxyNameBase
            """);

        provider.setFromClause("""
                from ap_item ai
                join dp_app_seqbackup seq on ai.trns_key = seq.documentid
                left join (
                    select documentid, MIN(sequence) as min_a_seq
                    from dp_app_seqbackup
                    where approvaltype = 'a'
                    group by documentid
                ) a on seq.documentid = a.documentid
            """);
        provider.setWhereClause("""
                where ai.TRNS_SRC = 'TRNS_SUNJIN_APPR' 
                  and seq.sequence > 1 
                  AND seq.documentid BETWEEN :minId AND :maxId
            """);

        Map<String, Order> sort = new HashMap<>();
        sort.put("seq.DocumentID", Order.ASCENDING);
        sort.put("seq.sequence", Order.ASCENDING);
        sort.put("seq.ApprovalTag", Order.ASCENDING);
        provider.setSortKeys(sort);

        return new JdbcPagingItemReaderBuilder<DocLineRow>()
                .name("docLineReader")
                .dataSource(dataSource)
                .pageSize(5000)
                .queryProvider(provider)
                .rowMapper((rs, rowNum) -> new DocLineRow(
                        rs.getLong("DocumentID"),
                        rs.getInt("sequence"),
                        rs.getLong("userId"),
                        rs.getString("nameBase"),
                        toLocalDateTimeSafe(rs.getTimestamp("ViewingTime")),
                        toLocalDateTimeSafe(rs.getTimestamp("ApprovalDate")),
                        rs.getString("ApprovalTag"),
                        rs.getString("ApprovalType"),
                        rs.getString("Proxy"),
                        rs.getInt("ProxyUserID"),
                        rs.getString("proxyNameBase")
                )).parameterValues(Map.of(
                        "minId", minId,
                        "maxId", maxId
                ))
                .build();
    }

    @Bean("docLinePagingReader")
    @StepScope
    public JdbcPagingItemReader<DocLineRow> docLineReader(
    ) {

        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
                seq.DocumentID,
                seq.sequence,
                seq.userId,
                seq.nameBase,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN NULL
                    ELSE seq.ViewingTime
                END AS ViewingTime,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN NULL
                    ELSE seq.ApprovalDate
                END AS ApprovalDate,
                CASE 
                    WHEN a.min_a_seq IS NOT NULL AND seq.sequence > a.min_a_seq THEN "READY"
                    ELSE seq.ApprovalType  
                END AS ApprovalType, 
                seq.ApprovalTag,
                seq.Proxy,
                seq.ProxyUserID,
                seq.proxyNameBase
            """);

        provider.setFromClause("""
                from ap_item ai
                join dp_app_seqbackup seq on ai.trns_key = seq.documentid
                left join (
                    select documentid, MIN(sequence) as min_a_seq
                    from dp_app_seqbackup
                    where approvaltype = 'a'
                    group by documentid
                ) a on seq.documentid = a.documentid
            """);
        provider.setWhereClause("""
                where ai.TRNS_SRC = 'TRNS_SUNJIN_APPR' 
                  and seq.sequence > 1
            """);

        Map<String, Order> sort = new HashMap<>();
        sort.put("seq.DocumentID", Order.ASCENDING);
        sort.put("seq.sequence", Order.ASCENDING);
        sort.put("seq.ApprovalTag", Order.ASCENDING);
        provider.setSortKeys(sort);

        return new JdbcPagingItemReaderBuilder<DocLineRow>()
                .name("docLineReader")
                .dataSource(dataSource)
                .pageSize(5000)
                .queryProvider(provider)
                .rowMapper((rs, rowNum) -> new DocLineRow(
                        rs.getLong("DocumentID"),
                        rs.getInt("sequence"),
                        rs.getLong("userId"),
                        rs.getString("nameBase"),
                        toLocalDateTimeSafe(rs.getTimestamp("ViewingTime")),
                        toLocalDateTimeSafe(rs.getTimestamp("ApprovalDate")),
                        rs.getString("ApprovalTag"),
                        rs.getString("ApprovalType"),
                        rs.getString("Proxy"),
                        rs.getInt("ProxyUserID"),
                        rs.getString("proxyNameBase")
                )).build();
    }

    private LocalDateTime toLocalDateTimeSafe(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}
