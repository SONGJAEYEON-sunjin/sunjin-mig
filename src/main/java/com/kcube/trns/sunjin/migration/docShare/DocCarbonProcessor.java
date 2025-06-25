package com.kcube.trns.sunjin.migration.docShare;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import com.kcube.trns.sunjin.cache.folder.FolderInfo;
import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import com.kcube.trns.sunjin.common.UserXid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DocCarbonProcessor implements ItemProcessor<DocShareRow, MapSqlParameterSource> {

    private final MigrationCache cache;
    private final DocItemKeyCache itemKeyCache;

    private final UserXid userXid;

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    @Value("${migration.default-kmid}")
    private Long defaultKmid;


    @Override
    public MapSqlParameterSource process(DocShareRow item) throws Exception {

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));
        FolderInfo folder = cache.getFolderCacheByTrnsKey(String.valueOf(item.deptid()));

        long groupId = makeGroupId(item, user, folder);
        String groupName = makeGroupName(item, user, folder);

        if(item.documentId() % 1000 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }


        MapSqlParameterSource param = new MapSqlParameterSource();
        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.documentId()));
        param.addValue("ITEMID", itemId == null ? item.documentId() : itemId);

        param.addValue("GROUPID", groupId);
        param.addValue("GROUP_NAME", groupName);
        param.addValue("TYPE", "REFERENCED");
        param.addValue("RGST_DATE", item.shareDate());

        return param;
    }

    private long makeGroupId(DocShareRow item, UserInfo user, FolderInfo folder) {
        if (isMember(item)) {
            return user != null && user.userId() != null
                    ? userXid.makeUserXid(user.userId())
                    : userXid.makeUserXid(defaultUserId);
        }

        Long folderKmId = (folder != null && folder.kmId() != null)
                ? folder.kmId()
                : defaultKmid;
        return item.isSubTree() == 0
                ? userXid.getExactDprtXid(folderKmId)
                : userXid.getBelowDprtXid(folderKmId);
    }

    private String makeGroupName(DocShareRow item, UserInfo user, FolderInfo folder) {
        if (isMember(item)) {
            return (user != null && user.userDisp() != null)
                    ? user.userDisp()
                    : item.nameBase();
        }

        FolderInfo defaultFolder = cache.getFolderCache(defaultKmid);
        return (folder != null && folder.name() != null) ? folder.name() : defaultFolder.name();
    }

    private boolean isMember(DocShareRow item){
        return "M".equalsIgnoreCase(item.isGroup());
    }

    @Bean
    public SkipListener<Object, Object> skipLogger() {
        return new SkipListener<>() {
            @Override
            public void onSkipInRead(Throwable t) {
                log.warn(">> Skipped in READ: {}", t.getMessage());
            }

            @Override
            public void onSkipInProcess(Object item, Throwable t) {
                log.warn(">> Skipped in PROCESS: {}, reason: {}", item, t.getMessage());
            }

            @Override
            public void onSkipInWrite(Object item, Throwable t) {
                log.warn(">> Skipped in WRITE: {}, reason: {}", item, t.getMessage());
            }
        };
    }

    @Bean
    public StepExecutionListener stepStatsLogger() {
        return new StepExecutionListener() {
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("=== Step Stats ===");
                log.info("Read Count     : {}", stepExecution.getReadCount());
                log.info("Filter Count   : {}", stepExecution.getFilterCount()); // processor에서 null 리턴된 수
                log.info("Write Count    : {}", stepExecution.getWriteCount());
                log.info("Skip Count     : {}", stepExecution.getSkipCount());   // exception으로 skip된 수
                return stepExecution.getExitStatus();
            }
        };
    }
}
