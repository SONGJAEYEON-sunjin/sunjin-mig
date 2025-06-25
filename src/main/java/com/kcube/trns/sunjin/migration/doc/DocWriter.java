package com.kcube.trns.sunjin.migration.doc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
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
public class DocWriter {

    @Bean("docRowBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> dorRowWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .sql("""
                INSERT INTO ap_item(
                    ORGID, FORMID, FORM_NAME, GRPID,
                    USERID, USER_NAME, USER_DISP, DPRTID, DPRT_NAME,
                    RGST_DATE, LAST_DATE, REQ_DATE, COMP_DATE,
                    TITLE, STATUS, FILE_CNT, FILE_EXT,
                    SHARE_YN, HIDE_YN, DEL_YN, RCV_YN, RCV_CNT,
                    WEB_HTML, FIELD_VALUES, DOCNO, EXPR_MONTH,
                    SCRT_TYPE, SIGN_TYPE, TENANTID, TRNS_SRC, TRNS_KEY, SYNCID,
                    CTGRID, CTGR_DPRTID, CTGR_DPRT_NAME,
                    LDGR_ACTN_YN, LDGR_SEND_YN, FAKE_YN
                ) VALUES (
                    :ORGID, :FORMID, :FORM_NAME, :GRPID, 
                    :USERID, :USER_NAME, :USER_DISP, :DPRTID, :DPRT_NAME,
                    :RGST_DATE, :LAST_DATE, :REQ_DATE, :COMP_DATE,
                    :TITLE, :STATUS, :FILE_CNT, :FILE_EXT,
                    :SHARE_YN, :HIDE_YN, :DEL_YN, :RCV_YN, :RCV_CNT,
                    :WEB_HTML, :FIELD_VALUES, :DOCNO, :EXPR_MONTH,
                    :SCRT_TYPE, :SIGN_TYPE, :TENANTID, :TRNS_SRC, :TRNS_KEY, :SYNCID,
                    :CTGRID, :CTGR_DPRTID, :CTGR_DPRT_NAME,
                    :LDGR_ACTN_YN, :LDGR_SEND_YN, :FAKE_YN
                )
            """)
                .itemSqlParameterSourceProvider(item -> item)
                .dataSource(dataSource)
                .build();
    }

    @Bean("apItemBatchWriter")
    public JdbcBatchItemWriter<MapSqlParameterSource> apItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<MapSqlParameterSource>()
                .sql("""
            INSERT INTO doc_item (
                ORGID, APPRID, FORMID, FORM_NAME, GRPID, USERID, USER_NAME, USER_DISP,
                DPRTID, DPRT_NAME, RGST_DATE, LAST_DATE, REQ_DATE, TITLE, DOC_TYPE,
                FILE_CNT, FILE_EXT, SHARE_YN, DEL_YN, SAVE_PATH, WEB_HTML, FIELD_VALUES,
                DOCNO, EXPR_MONTH, SCRT_TYPE, SIGN_TYPE, SIGN_SAVE_PATH,
                TRNS_SRC, TRNS_KEY, CTGRID, CTGR_DPRTID, CTGR_DPRT_NAME, FORM_OPT,
                TENANTID
            ) VALUES (
                :ORGID, :APPRID, :FORMID, :FORM_NAME, :GRPID, :USERID, :USER_NAME, :USER_DISP,
                :DPRTID, :DPRT_NAME, :RGST_DATE, :LAST_DATE, :REQ_DATE, :TITLE, :DOC_TYPE,
                :FILE_CNT, :FILE_EXT, :SHARE_YN, :DEL_YN, :SAVE_PATH, :WEB_HTML, :FIELD_VALUES,
                :DOCNO, :EXPR_MONTH, :SCRT_TYPE, :SIGN_TYPE, :SIGN_SAVE_PATH,
                :TRNS_SRC, :TRNS_KEY, :CTGRID, :CTGR_DPRTID, :CTGR_DPRT_NAME, :FORM_OPT,
                :TENANTID
            )
        """)
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(item -> item)
                .build();
    }


    @Bean
    public StepExecutionListener docItemStepLogger() {
        return new StepExecutionListener() {
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info(">>> Step Summary");
                log.info("Read Count: {}", stepExecution.getReadCount());
                log.info("Filter Count (null returned): {}", stepExecution.getFilterCount());
                log.info("Write Count: {}", stepExecution.getWriteCount());
                log.info("Skip Count: {}", stepExecution.getSkipCount());
                return stepExecution.getExitStatus();
            }
        };
    }
}

