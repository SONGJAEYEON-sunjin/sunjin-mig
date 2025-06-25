package com.kcube.trns.sunjin.migration.docRfrn;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocRfrnProcessor implements ItemProcessor<DocRfrnRow, MapSqlParameterSource> {

    private final DocItemKeyCache itemKeyCache;

    @Override
    public MapSqlParameterSource process(DocRfrnRow item) throws Exception {

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.itemId()));
        Long docItemId = itemKeyCache.getDocIdByTrnskey(String.valueOf(itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.rfrnItemId()))));

        if(itemId == null || docItemId == null) {
            log.info(">>>>>>>>>>>>>>>>>>>>>>>> item.documentId() : {}, ap_item.itemid : {}, doc_item.itemid : {}  ",item.itemId(),itemId,docItemId);
        }

        String title = StringEscapeUtils.unescapeHtml4(item.title());

        return new MapSqlParameterSource()
                .addValue("ITEMID", itemId == null ? item.itemId() : itemId)
                .addValue("RFRN_ITEMID", docItemId == null ? item.rfrnItemId() : docItemId)
                .addValue("TITLE", title)
                .addValue("DIVISION", "dprtDoc");
    }
}
