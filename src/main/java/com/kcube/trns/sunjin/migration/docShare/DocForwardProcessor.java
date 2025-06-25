package com.kcube.trns.sunjin.migration.docShare;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import com.kcube.trns.sunjin.common.UserXid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class DocForwardProcessor implements ItemProcessor<DocShareRow, MapSqlParameterSource> {
    private final MigrationCache cache;
    private final DocItemKeyCache itemKeyCache;
    private final UserXid userXid;

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    @Override
    public MapSqlParameterSource process(DocShareRow item) throws Exception {

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));

        MapSqlParameterSource param = new MapSqlParameterSource();

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.documentId()));
        param.addValue("ITEMID", itemId == null ? item.documentId() : itemId);

        if(item.documentId() % 1000 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }

        if(user == null || user.userId() == null) {
            user = cache.getUserInfo(defaultUserId);
            param.addValue("GROUP_NAME", item.nameBase());
            param.addValue("GROUPID", userXid.makeUserXid(user.userId()));
        }else{
            param.addValue("GROUP_NAME", user.name());
            param.addValue("GROUPID", userXid.makeUserXid(user.userId()));
        }

        param.addValue("TYPE", "SHARED");
        param.addValue("RGST_DATE", item.shareDate());

        return param;
    }
}
