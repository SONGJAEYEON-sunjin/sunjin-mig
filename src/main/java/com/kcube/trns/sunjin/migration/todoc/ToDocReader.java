package com.kcube.trns.sunjin.migration.todoc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
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
public class ToDocReader {

    private final DataSource dataSource;

    @Bean
    @StepScope
    public JdbcPagingItemReader<ApItem> toDocRowReader(
    ) {
        JdbcPagingItemReader<ApItem> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(3000);

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

        MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
        provider.setSelectClause("SELECT *");
        provider.setFromClause("FROM ap_item");
        provider.setWhereClause("""
            where trns_key is not null 
            and itemid BETWEEN :minId and :maxId
        """);
        provider.setSortKeys(Map.of("ITEMID", Order.ASCENDING));

        reader.setQueryProvider(provider);
        return reader;
    }

    private LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
