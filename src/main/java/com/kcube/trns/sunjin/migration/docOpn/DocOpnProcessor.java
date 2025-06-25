package com.kcube.trns.sunjin.migration.docOpn;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocOpnProcessor implements ItemProcessor<DocOpnRow, MapSqlParameterSource> {

    private final MigrationCache cache;
    private final DocItemKeyCache itemKeyCache;

    private Long lastDocumentId = null;
    private int sortIndex = 0;

    private static final int CONTENT_MAX = 2000;

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    @Override
    public MapSqlParameterSource process(DocOpnRow item) {

        if (!Objects.equals(item.documentId(), lastDocumentId)) {
            lastDocumentId = item.documentId();
            sortIndex = 0;
        }

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));

        String type = switch (item.section()) {
            case 1 -> "AP";
            case 2 -> "COMMENT";
            default -> null;
        };

        String content = item.content();
        if (content != null) {
            content = StringEscapeUtils.unescapeHtml4(content);

            content = content.replace("<br>", "\n")
                    .replace("<br/>", "\n")
                    .replace("<br />", "\n")
                    .replace("&nbsp;", " ");

            if (content.length() > CONTENT_MAX) {
                log.warn(" CONTENT 잘림: docId={}, originalLength={}", item.documentId(), content.length());
                content = content.substring(0, CONTENT_MAX);
            }
        }


        MapSqlParameterSource param = new MapSqlParameterSource();

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.documentId()));
        param.addValue("ITEMID", itemId == null ? item.documentId() : itemId);

        if(item.documentId() % 10000 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }

        param.addValue("GID", null);
        param.addValue("TYPE", type);
        param.addValue("CONTENT", content);
        param.addValue("SORT", 0);
        param.addValue("RATE", 0);

        if(user == null || user.userId() == null) {
            user = cache.getUserInfo(defaultUserId);

            param.addValue("USERID", user.userId());
            param.addValue("USER_NAME", item.nameBase());
            param.addValue("USER_DISP", item.nameBase());

        }else{
            param.addValue("USERID", user.userId());
            param.addValue("USER_NAME", user.name());
            param.addValue("USER_DISP", user.userDisp());
        }
        param.addValue("RGST_DATE", item.writeTime());
        param.addValue("LAST_DATE", item.writeTime());
        param.addValue("IMMUTABLE", 1);

        return param;
    }
}
