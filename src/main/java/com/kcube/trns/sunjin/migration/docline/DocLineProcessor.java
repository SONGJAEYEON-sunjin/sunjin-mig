package com.kcube.trns.sunjin.migration.docline;

import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocLineProcessor implements ItemProcessor<DocLineRow, MapSqlParameterSource> {

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    @Value("${migration.default-kmid}")
    private Long defaultKmid;


    private final MigrationCache cache;
    private final DocItemKeyCache itemKeyCache;
    @Override
    public MapSqlParameterSource process(DocLineRow item) {

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));
        UserInfo proxyUser = cache.getUserInfoByTrnsKey(String.valueOf(item.proxyUserId()));

        if(item.documentId() % 10000 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }

        String action = "";

        if(item.approvalTag().startsWith("ZD")){
            if(item.approvalType().equalsIgnoreCase("A")){
                action = "DC";
            }else{
                action = "AP";
            }
        }else{
            action = "CP";
        }

        int sort = item.sequence() - 1;

        MapSqlParameterSource params = new MapSqlParameterSource();

        Long itemId = itemKeyCache.getItemIdCacheByTrnsKey(String.valueOf(item.documentId()));
        params.addValue("ITEMID", itemId == null ? item.documentId() : itemId);

        if(user == null || user.userId() == null) {

            user = cache.getUserInfo(defaultUserId);
                params.addValue("USERID", user.userId());

                if(item.nameBase() == null){
                    params.addValue("USERID", user.userId());
                    params.addValue("USER_NAME", "'{\"ko\":\"퇴사자\",\"en\":\"Retiree\",\"zh\":\"退休人员\",\"ja\":\"退社者\"}'");
                    params.addValue("USER_DISP", "'{\"ko\":\"퇴사자\",\"en\":\"Retiree\",\"zh\":\"退休人员\",\"ja\":\"退社者\"}'");
                }else{
                    params.addValue("USERID", user.userId());
                    params.addValue("USER_NAME", item.nameBase());
                    params.addValue("USER_DISP", item.nameBase());
                }
                params.addValue("DPRTID", defaultKmid);
                params.addValue("DPRT_NAME", "선진");
                params.addValue("PSTNID", user.pstnId());
                params.addValue("PSTN_NAME", user.pstnName());
                params.addValue("GRADEID", user.gradeId());
                params.addValue("GRADE_NAME", user.gradeId());
        }else{
            params.addValue("USERID", user.userId());
            params.addValue("USER_NAME", user.name());
            params.addValue("USER_DISP", user.userDisp());
            params.addValue("DPRTID", user.dprtId());
            params.addValue("DPRT_NAME", user.dprtName());
            params.addValue("PSTNID", user.pstnId());
            params.addValue("PSTN_NAME", user.pstnName());
            params.addValue("GRADEID", user.gradeId());
            params.addValue("GRADE_NAME", user.gradeId());
        }

        params.addValue("ACTION", action);
        params.addValue("SORT", sort);
        params.addValue("STEP", sort);
        params.addValue("READ_DATE", item.viewingTime());
        params.addValue("ARR_DATE", item.viewingTime());
        params.addValue("COMP_DATE", item.approvalDate());
        params.addValue("STATUS", item.approvalType().equalsIgnoreCase("READY") ? "READY" : "SIGN");
        params.addValue("SIGN_TYPE", "ALL");
        params.addValue("SIGN_SAVE_PATH", null);
        params.addValue("ACCTID", null);
        params.addValue("ACCT_YN","N");
        params.addValue("ACCT_DATE", null);
        params.addValue("CRNT_YN", "N");

        if( item.proxy() != null && item.proxy().equalsIgnoreCase("Y")){
            if(proxyUser == null || proxyUser.userId() == null) {
                proxyUser = cache.getUserInfo(defaultUserId);
                params.addValue("AGNT_YN", "Y");
                params.addValue("AGNT_USERID", proxyUser.userId());
                params.addValue("AGNT_USER_NAME", item.proxyNameBase());
                params.addValue("AGNT_USER_DISP", item.proxyNameBase());
                params.addValue("AGNT_PSTN_NAME", proxyUser.pstnName());
                params.addValue("AGNT_GRADE_NAME", proxyUser.gradeName());
            }else{
                params.addValue("AGNT_YN", "Y");
                params.addValue("AGNT_USERID", proxyUser.userId());
                params.addValue("AGNT_USER_NAME", proxyUser.name());
                params.addValue("AGNT_USER_DISP", proxyUser.userDisp());
                params.addValue("AGNT_PSTN_NAME", proxyUser.pstnName());
                params.addValue("AGNT_GRADE_NAME", proxyUser.gradeName());
            }
        }else{
            params.addValue("AGNT_YN", "N");
            params.addValue("AGNT_USERID", null);
            params.addValue("AGNT_USER_NAME", null);
            params.addValue("AGNT_USER_DISP", null);
            params.addValue("AGNT_PSTN_NAME", null);
            params.addValue("AGNT_GRADE_NAME", null);
        }

        return params;
    }
}