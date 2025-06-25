package com.kcube.trns.sunjin.migration.docFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocFileWriter {

    @Bean("docFileBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> apItemFileWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .sql("""
                INSERT INTO ap_item_file
                (ITEMID, FILE_NAME, FILE_SIZE, DNLD_CNT, SAVE_PATH, EDITOR_YN)
                VALUES (:ITEMID, :FILE_NAME, :FILE_SIZE, :DNLD_CNT, :SAVE_PATH, :EDITOR_YN)
            """)
                .itemSqlParameterSourceProvider(item -> item)
                .dataSource(dataSource)
                .build();
    }

}
