package com.kcube.trns.sunjin.migration.docRcvr;

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

@Component
@RequiredArgsConstructor
@Slf4j
public class DocRcvrProcessor implements ItemProcessor<DocRcvrRow, MapSqlParameterSource> {

    private final MigrationCache cache;
    private final DocItemKeyCache itemKeyCache;

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    private final UserXid userXid;

    @Override
    public MapSqlParameterSource process(DocRcvrRow item) {

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));


        MapSqlParameterSource params = new MapSqlParameterSource();

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.itemId()));
        params.addValue("ITEMID", itemId == null ? item.itemId() : itemId);

        Long acctId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.acctId()));
        params.addValue("ITEMID", acctId == null ? item.acctId() : acctId);

        params.addValue("ITEMID", itemId);
        params.addValue("ACCTID", acctId);
        params.addValue("ACCT_YN", item.acctYn());
        params.addValue("ACCT_DATE", item.acctDate());

        if(item.itemId() % 10000 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.itemId());
        }

        if(user == null || user.userId() == null){
            user = cache.getUserInfo(defaultUserId);
            params.addValue("GROUPID", userXid.makeUserXid(user.userId()));
            params.addValue("GROUP_NAME", item.nameBase());
        }else{
            params.addValue("GROUPID", userXid.makeUserXid(user.userId()));
            params.addValue("GROUP_NAME", user.userDisp());
        }

        return params;
    }
}
