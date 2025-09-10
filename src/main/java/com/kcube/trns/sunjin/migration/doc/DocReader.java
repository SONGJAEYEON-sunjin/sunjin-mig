package com.kcube.trns.sunjin.migration.doc;

import com.kcube.trns.sunjin.migration.todoc.ApItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocReader {

    private final DataSource dataSource;

    @Value("${migration.tobe-min-itemid}")
    private Long tobeMinItemid;

    @Value("${migration.tobe-max-itemid}")
    private Long tobeMaxItemid;

    @Bean("docRowReader")
    @StepScope
    public JdbcPagingItemReader<DocRow> docRowReader(
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
            SELECT documentid,
                   userid,
                   parentdocumentid,
                   accesstitleid,
                   subject AS title,
                   namebase,
                   contenthtml,
                   writetime,
                   completedate,
                   docnumber,
                   formId,
                   deptcodeid
        """);
        provider.setFromClause("FROM dp_app_doc");
        provider.setWhereClause("""
              where approvalState = 'C'
                        """);
        provider.setSortKeys(Map.of("documentid", Order.ASCENDING));

        JdbcPagingItemReader<DocRow> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(2000);

        reader.setQueryProvider(provider);
        reader.setRowMapper((rs, rowNum) -> new DocRow(
                rs.getLong("documentid"),
                rs.getLong("userid"),
                rs.getLong("parentdocumentid"),
                rs.getString("accesstitleid"),
                rs.getString("title"),
                rs.getString("namebase"),
                rs.getTimestamp("writetime"),
                rs.getTimestamp("completedate"),
                rs.getString("contenthtml"),
                rs.getString("docNumber"),
                rs.getLong("formid"),
                rs.getLong("deptcodeid")
        ));

        return reader;
    }

    @Bean("docRowPartitionReader")
    @StepScope
    public JdbcPagingItemReader<DocRow> docRowPartitionReader(
        @Value("#{stepExecutionContext[minId]}") Long minId,
        @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) {
        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("""
            SELECT documentid, 
                   userid, 
                   parentdocumentid, 
                   accesstitleid, 
                   subject AS title,
                   namebase,
                   contenthtml, 
                   writetime, 
                   completedate, 
                   docnumber, 
                   formId,
                   deptcodeid
        """);
        provider.setFromClause("FROM dp_app_doc");
        provider.setWhereClause("""
              where documentid BETWEEN :minId and :maxId
              AND approvalState = 'C'
                        """);
        provider.setSortKeys(Map.of("documentid", Order.ASCENDING));

        JdbcPagingItemReader<DocRow> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(2000);

        reader.setQueryProvider(provider);
        reader.setRowMapper((rs, rowNum) -> new DocRow(
                rs.getLong("documentid"),
                rs.getLong("userid"),
                rs.getLong("parentdocumentid"),
                rs.getString("accesstitleid"),
                rs.getString("title"),
                rs.getString("namebase"),
                rs.getTimestamp("writetime"),
                rs.getTimestamp("completedate"),
                rs.getString("contenthtml"),
                rs.getString("docNumber"),
                rs.getLong("formid"),
                rs.getLong("deptcodeid")
        ));

        reader.setParameterValues(Map.of(
                "minId", minId,
                "maxId", maxId
        ));
        return reader;
    }

    @Bean
    public JdbcPagingItemReader<ApItem> apItemReader(DataSource dataSource) {
        JdbcPagingItemReader<ApItem> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(4000);

        reader.setRowMapper((rs, rowNum) -> new ApItem(
                rs.getLong("ORGID"),
                rs.getLong("ITEMID"),
                rs.getLong("FORMID"),
                rs.getString("FORM_NAME"),
                rs.getLong("GRPID"),
                rs.getLong("USERID"),
                rs.getString("USER_NAME"),
                rs.getString("USER_DISP"),
                rs.getLong("DPRTID"),
                rs.getString("DPRT_NAME"),
                toLocalDateTime(rs.getTimestamp("RGST_DATE")),
                toLocalDateTime(rs.getTimestamp("LAST_DATE")),
                toLocalDateTime(rs.getTimestamp("REQ_DATE")),
                rs.getString("TITLE"),
                rs.getInt("FILE_CNT"),
                rs.getString("FILE_EXT"),
                rs.getString("SHARE_YN"),
                rs.getString("DEL_YN"),
                rs.getString("SAVE_PATH"),
                rs.getString("WEB_HTML"),
                rs.getString("FIELD_VALUES"),
                rs.getString("DOCNO"),
                rs.getInt("EXPR_MONTH"),
                rs.getString("SCRT_TYPE"),
                rs.getString("SIGN_TYPE"),
                rs.getString("SIGN_SAVE_PATH"),
                rs.getString("CTGRID"),
                rs.getLong("CTGR_DPRTID"),
                rs.getString("CTGR_DPRT_NAME"),
                rs.getString("FORM_OPT"),
                rs.getString("M_CONTENT"),
                rs.getLong("TENANTID"),
                rs.getString("TRNS_KEY")
        ));

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("SELECT *");
        queryProvider.setFromClause("FROM ap_item");
        queryProvider.setWhereClause("where trns_key is not null and itemid between :minId and :maxId ");
        queryProvider.setSortKeys(Map.of("ITEMID", Order.ASCENDING));



        reader.setParameterValues(Map.of(
                "minId", tobeMinItemid,
                "maxId", tobeMaxItemid
        ));

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
