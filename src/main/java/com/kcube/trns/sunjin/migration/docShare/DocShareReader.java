package com.kcube.trns.sunjin.migration.docShare;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DocShareReader {

    @Bean("docCirDocPagingReader")
    public JdbcPagingItemReader<DocShareRow> docCirDocReader(DataSource dataSource) throws Exception {

        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT  cd.circulationdocumentid, cd.DocumentID, cu.UserID, cu.NameBase, cd.CirculationDate");
        provider.setFromClause("""
            from dp_app_doc d join dp_app_cirdoc cd
            on d.documentid = cd.documentid
            join dp_app_circuser cu
            on cd.circulationdocumentid = cu.CirculationDocumentID
        """);
        provider.setWhereClause("""
            where d.approvalState = 'C' 
        """);
        provider.setSortKeys(Map.of(
                "cd.circulationdocumentid", Order.ASCENDING,
                "cd.documentid", Order.ASCENDING,
                "cu.userid",Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocShareRow>()
                .name("docCirDocReader")
                .dataSource(dataSource)
                .queryProvider(provider.getObject())
                .rowMapper((rs, rowNum) -> new DocShareRow(
                        rs.getLong("DocumentID"),
                        rs.getLong("UserID"),
                        rs.getString("NameBase"),
                        rs.getTimestamp("CirculationDate").toLocalDateTime(),
                        null,
                        null,
                        0
                ))
                .pageSize(800)
                .build();
    }

    @Bean("docForwardPagingReader")
    public JdbcPagingItemReader<DocShareRow> docForwardReader(DataSource dataSource) throws Exception {

        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("SELECT fu.ForwardDocumentID, fu.documentid,fu.ReceiveUserID, fu.NameBase, fu.WriteDate");
        provider.setFromClause("""
            from dp_app_doc d join  dp_app_forwarduser fu
             on d.documentid = fu.documentid
        """);
        provider.setWhereClause("""
             where d.approvalState = 'C' 
        """);
        provider.setSortKeys(Map.of(
                "fu.documentid", Order.ASCENDING,
                "fu.ForwardDocumentID", Order.ASCENDING,
                "fu.ReceiveUserID", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocShareRow>()
                .name("docForwardReader")
                .dataSource(dataSource)
                .queryProvider(provider.getObject())
                .rowMapper((rs, rowNum) -> new DocShareRow(
                        rs.getLong("DocumentID"),
                        rs.getLong("ReceiveUserID"),
                        rs.getString("NameBase"),
                        rs.getTimestamp("WriteDate").toLocalDateTime(),
                        null,
                        null,
                        0
                ))
                .pageSize(1000)
                .build();
    }

    @Bean("docCarbonPartitionReader")
    @StepScope
    public JdbcPagingItemReader<DocShareRow> docCarbonPartitionReader(
            DataSource dataSource,
            @Value("#{stepExecutionContext[minId]}") Long minId,
            @Value("#{stepExecutionContext[maxId]}") Long maxId
    ) throws Exception {

        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
        SELECT ccu.DocumentID,
               ccu.UserID, 
               ccu.NameBase, 
               d.writetime, 
               ccu.DeptId, 
               ccu.isGroup, 
               ccu.isSubTree
        """);
        provider.setFromClause("""
            from dp_app_doc d join DP_APP_CarbonCopyUser ccu
            on d.documentid = ccu.documentid
        """);
        provider.setWhereClause("""
            where d.approvalState = 'C' 
            AND ccu.documentid BETWEEN :minId AND :maxId
        """);
        provider.setSortKeys(Map.of(
                "ccu.DocumentID", Order.ASCENDING,
                "ccu.UserID", Order.ASCENDING,
                "ccu.deptID", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocShareRow>()
                .name("docCarbonReader")
                .dataSource(dataSource)
                .queryProvider(provider.getObject())
                .rowMapper((rs, rowNum) -> new DocShareRow(
                        rs.getLong("DocumentID"),
                        rs.getLong("UserID"),
                        rs.getString("NameBase"),
                        rs.getTimestamp("writetime").toLocalDateTime(),
                        rs.getLong("DeptId"),
                        rs.getString("isGroup"),
                        rs.getInt("isSubTree")
                ))
                .parameterValues(Map.of(
                        "minId", minId,
                        "maxId", maxId
                )).pageSize(5000)
                .build();
    }

    @Bean("docCarbonPagingReader")
    @StepScope
    public JdbcPagingItemReader<DocShareRow> docCarbonReader(
            DataSource dataSource
    ) throws Exception {

        SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
        provider.setDataSource(dataSource);
        provider.setSelectClause("""
        SELECT ccu.DocumentID,
               ccu.UserID, 
               ccu.NameBase, 
               d.writetime, 
               ccu.DeptId, 
               ccu.isGroup, 
               ccu.isSubTree
        """);
        provider.setFromClause("""
            from dp_app_doc d join DP_APP_CarbonCopyUser ccu
            on d.documentid = ccu.documentid
        """);
        provider.setWhereClause("""
            where d.approvalState = 'C' 
            AND ccu.documentid BETWEEN :minId AND :maxId
        """);
        provider.setSortKeys(Map.of(
                "ccu.DocumentID", Order.ASCENDING,
                "ccu.UserID", Order.ASCENDING,
                "ccu.deptID", Order.ASCENDING
        ));

        return new JdbcPagingItemReaderBuilder<DocShareRow>()
                .name("docCarbonReader")
                .dataSource(dataSource)
                .queryProvider(provider.getObject())
                .rowMapper((rs, rowNum) -> new DocShareRow(
                        rs.getLong("DocumentID"),
                        rs.getLong("UserID"),
                        rs.getString("NameBase"),
                        rs.getTimestamp("writetime").toLocalDateTime(),
                        rs.getLong("DeptId"),
                        rs.getString("isGroup"),
                        rs.getInt("isSubTree")
                )).pageSize(5000)
                .build();
    }
}