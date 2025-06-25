package com.kcube.trns.sunjin.migration.docSrch;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DocSrchReader {

    @Bean("docSrchPartitionReader")
    @StepScope
    public JdbcPagingItemReader<DocSrchDto> docSrchPartitionReader(
            DataSource dataSource,
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("itemid, web_html");
        provider.setFromClause("FROM ap_item");
        provider.setWhereClause("WHERE trns_src = 'TRNS_SUNJIN_APPR' and itemid BETWEEN :minId AND :maxId");
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("itemid", Order.ASCENDING);
        provider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<DocSrchDto>()
                .name("docSrchPagingReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) ->
                        new DocSrchDto(
                                rs.getLong("itemid"),
                                rs.getString("web_html")
                        )
                ).parameterValues(Map.of(
                        "minId", minId,
                        "maxId", maxId
                ))
                .build();
    }


    @Bean("docSrchPagingReader")
    @StepScope
    public JdbcPagingItemReader<DocSrchDto> docSrchPagingReader(
            DataSource dataSource
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("itemid, web_html");
        provider.setFromClause("FROM ap_item");
        provider.setWhereClause("WHERE trns_src = 'TRNS_SUNJIN_APPR' and itemid BETWEEN ${minId} and ${maxId}");
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("itemid", Order.ASCENDING);
        provider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<DocSrchDto>()
                .name("docSrchPagingReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) ->
                        new DocSrchDto(
                                rs.getLong("itemid"),
                                rs.getString("web_html")
                        )
                )
                .build();
    }

    public record DocSrchDto(
            Long itemId,
            String webHtml
    ){
    }
}
