package com.kcube.trns.sunjin.migration.docFile;

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
public class DocFileReader {
    private final DataSource dataSource;

    @Bean("docFilePagingReader")
    public JdbcPagingItemReader<DocFileRow> apItemFileReader(DataSource dataSource) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
            da.attachfileid,
            ai.itemid AS itemId,
            da.filename AS fileName,
            da.filelength AS fileSize,
            da.filepath AS filePath,
            da.fileguid AS fileGuid,
            da.fileOrder AS fileOrder
        """);
        provider.setFromClause("""
            FROM dp_app_attach da
            JOIN ap_item ai ON ai.trns_key = da.documentid
        """);
        provider.setWhereClause("""
            where ai.TRNS_SRC = 'TRNS_SUNJIN_APPR' 
        """);
        provider.setSortKeys(Map.of(
                "da.attachfileid", Order.ASCENDING,
                "da.fileOrder", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocFileRow>()
                .name("apItemFileReader")
                .dataSource(dataSource)
                .queryProvider(provider)
                .pageSize(5000)
                .rowMapper((rs, rowNum) -> new DocFileRow(
                        rs.getLong("itemId"),
                        rs.getString("fileName"),
                        rs.getLong("fileSize"),
                        rs.getString("filePath"),
                        rs.getString("fileGuid")
                ))
                .build();
    }
}
