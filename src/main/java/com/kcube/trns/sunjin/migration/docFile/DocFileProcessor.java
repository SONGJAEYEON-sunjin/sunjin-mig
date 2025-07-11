package com.kcube.trns.sunjin.migration.docFile;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocFileProcessor implements ItemProcessor<DocFileRow, MapSqlParameterSource> {

    private final DocItemKeyCache itemKeyCache;

    @Override
    public MapSqlParameterSource process(DocFileRow item) throws Exception {

        String cleanPath = item.filePath();
        if (cleanPath != null && cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        cleanPath = cleanPath.replace("DeskPlusEIP", "");

        String combined = (cleanPath != null ? cleanPath : "") + item.fileGuid();

        String savePath = "DeskPlusFileServer" + combined;

        MapSqlParameterSource param = new MapSqlParameterSource();

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.itemId()));
        param.addValue("ITEMID", itemId == null ? item.itemId() : itemId);
        param.addValue("FILE_NAME", item.fileName());
        param.addValue("FILE_SIZE", item.fileSize());
        param.addValue("DNLD_CNT", 0);
        param.addValue("SAVE_PATH", savePath);
        param.addValue("EDITOR_YN", "N");

        return param;

    }
}
