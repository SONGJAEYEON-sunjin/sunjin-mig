package com.kcube.trns.sunjin.migration.docShare;

import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import com.kcube.trns.sunjin.cache.folder.DeptCodeInfo;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import com.kcube.trns.sunjin.common.UserXid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
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
//        FolderInfo folder = cache.getFolderCacheByTrnsKey(String.valueOf(item.deptid()));
        DeptCodeInfo deptCodeInfo = cache.getDeptCode(item.deptCodeId());


        long groupId = makeGroupId(item, user, deptCodeInfo);
        String groupName = makeGroupName(item, user, deptCodeInfo);

        if(item.documentId() % 100 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }


        MapSqlParameterSource param = new MapSqlParameterSource();
        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.documentId()));
        param.addValue("ITEMID", itemId == null ? item.documentId() : itemId);

        param.addValue("GROUPID", groupId);

        if(groupName == null){
            param.addValue("GROUP_NAME", "'{\"ko\":\"퇴사자\",\"en\":\"Retiree\",\"zh\":\"退休人员\",\"ja\":\"退社者\"}'");
        }else{
            param.addValue("GROUP_NAME", groupName);
        }

        param.addValue("TYPE", "REFERENCED");
        param.addValue("RGST_DATE", item.shareDate());

        return param;
    }

    private long makeGroupId(DocShareRow item, UserInfo user, DeptCodeInfo deptCodeInfo) {
        if (isMember(item)) {
            return user != null && user.userId() != null
                    ? userXid.makeUserXid(user.userId())
                    : userXid.makeUserXid(defaultUserId);
        }

        Long folderKmId = (deptCodeInfo != null && deptCodeInfo.kmId() != null)
                ? deptCodeInfo.kmId()
                : defaultKmid;
        return item.isSubTree() == 0
                ? userXid.getExactDprtXid(folderKmId)
                : userXid.getBelowDprtXid(folderKmId);
    }

    private String makeGroupName(DocShareRow item, UserInfo user, DeptCodeInfo deptCodeInfo) {
        if (isMember(item)) {
            return (user != null && user.userDisp() != null)
                    ? user.userDisp()
                    : item.nameBase();
        }

//        FolderInfo defaultFolder = cache.getFolderCache(defaultKmid);
//        return (folder != null && folder.name() != null) ? folder.name() : defaultFolder.name();
        return (deptCodeInfo != null && deptCodeInfo.name() != null) ? deptCodeInfo.name() : "선진";
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
}
