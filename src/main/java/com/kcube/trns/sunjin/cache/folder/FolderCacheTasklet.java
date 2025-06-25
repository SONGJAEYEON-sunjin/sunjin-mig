package com.kcube.trns.sunjin.cache.folder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FolderCacheTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final FolderCache folderCache;

    @Value("${migration.tenant-id}")
    private String tenantId;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // folerCache 초기화
        String folerQuery = "SELECT kmid, name, trns_src, trns_key FROM km WHERE tenantId = ? ORDER BY kmid";
        List<FolderInfo> folderInfoList = jdbcTemplate.query(folerQuery, new FolderRowMapper(), tenantId);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> folerCache.size : {} ",folderInfoList.size());

        for(FolderInfo folderInfo : folderInfoList){
            if (folderInfo.kmId() == null) {
                log.error("⚠️ FolderInfo with null kmId: {}", folderInfo);
                continue;
            }
            folderCache.putFolderCache(folderInfo.kmId(), folderInfo);
        }

        return RepeatStatus.FINISHED;
    }
}
