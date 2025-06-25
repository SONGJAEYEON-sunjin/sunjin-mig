package com.kcube.trns.sunjin.migration.docline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Configuration
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DocLineWriter {

    private final DataSource dataSource;

    @Bean("docLineBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> docLineWriter() {
        String sql = """
            INSERT INTO ap_item_line (
              ITEMID, USERID, USER_NAME, USER_DISP, DPRTID, DPRT_NAME,
              PSTNID, PSTN_NAME, GRADEID, GRADE_NAME, `ACTION`, SORT, STEP,
              READ_DATE, ARR_DATE, COMP_DATE, STATUS, SIGN_TYPE, SIGN_SAVE_PATH,
              ACCTID, ACCT_YN, ACCT_DATE, CRNT_YN, AGNT_YN,
              AGNT_USERID, AGNT_USER_NAME, AGNT_USER_DISP, AGNT_PSTN_NAME, AGNT_GRADE_NAME
            )
            VALUES (
              :ITEMID, :USERID, :USER_NAME, :USER_DISP, :DPRTID, :DPRT_NAME,
              :PSTNID, :PSTN_NAME, :GRADEID, :GRADE_NAME, :ACTION, :SORT, :STEP,
              :READ_DATE, :ARR_DATE, :COMP_DATE, :STATUS, :SIGN_TYPE, :SIGN_SAVE_PATH,
              :ACCTID, :ACCT_YN, :ACCT_DATE, :CRNT_YN, :AGNT_YN,
              :AGNT_USERID, :AGNT_USER_NAME, :AGNT_USER_DISP, :AGNT_PSTN_NAME, :AGNT_GRADE_NAME
            )
        """;
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .dataSource(dataSource)
                .sql(sql)
                .itemSqlParameterSourceProvider(item -> item)
                .build();
    }
}




