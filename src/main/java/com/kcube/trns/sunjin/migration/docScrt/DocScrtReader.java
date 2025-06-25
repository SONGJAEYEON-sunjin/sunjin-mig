package com.kcube.trns.sunjin.migration.docScrt;

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
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocScrtReader {

    @Bean("docScrtPagingReader")
    @StepScope
    public JdbcPagingItemReader<ApItem> docScrtPagingReader(
            DataSource dataSource
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("ITEMID, DPRTID, DPRT_NAME");
        provider.setFromClause("FROM ap_item");
        provider.setWhereClause("WHERE trns_src = 'TRNS_SUNJIN_APPR' and itemid BETWEEN ${minId} AND ${maxId}");
        provider.setSortKeys(Map.of("ITEMID", Order.ASCENDING));

        return new JdbcPagingItemReaderBuilder<ApItem>()
                .name("docScrtPagingReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) -> new ApItem(
                        rs.getLong("ITEMID"),
                        rs.getLong("DPRTID"),
                        rs.getString("DPRT_NAME")
                ))
                .build();
    }

    @Bean("docScrtPagingPartitionReader")
    @StepScope
    public JdbcPagingItemReader<ApItem> docScrtPagingPartitionReader(
            DataSource dataSource,
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("ITEMID, DPRTID, DPRT_NAME");
        provider.setFromClause("FROM ap_item");
        provider.setWhereClause("WHERE trns_src = 'TRNS_SUNJIN_APPR' and itemid BETWEEN :minId AND :maxId");
        provider.setSortKeys(Map.of("ITEMID", Order.ASCENDING));

        return new JdbcPagingItemReaderBuilder<ApItem>()
                .name("docScrtPagingReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) -> new ApItem(
                        rs.getLong("ITEMID"),
                        rs.getLong("DPRTID"),
                        rs.getString("DPRT_NAME")
                )).parameterValues(Map.of(
                        "minId", minId,
                        "maxId", maxId
                ))
                .build();
    }

    public record ApItem(
            Long itemId,
            Long dprtId,
            String dprtName
    ) {}
}
