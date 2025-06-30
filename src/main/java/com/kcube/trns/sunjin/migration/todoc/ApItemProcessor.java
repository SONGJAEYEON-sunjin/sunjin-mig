package com.kcube.trns.sunjin.migration.todoc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApItemProcessor implements ItemProcessor<ApItem, MapSqlParameterSource> {
    @Override
    public MapSqlParameterSource process(ApItem item) {

        try {
            if(item.itemid() % 10000 == 0 ){
                log.info(">>>>>>>>>>>>>>>>>>>>>>>> item.itemid() : {} ",item.itemid());
            }

            MapSqlParameterSource param = new MapSqlParameterSource();
            param.addValue("ORGID", item.orgid() > 0 ? item.orgid() : null);
            param.addValue("APPRID", item.itemid());
            param.addValue("FORMID", item.formid());
            param.addValue("FORM_NAME", item.formName());
            param.addValue("GRPID", item.grpid());
            param.addValue("USERID", item.userid());
            param.addValue("USER_NAME", item.userName());
            param.addValue("USER_DISP", item.userDisp());
            param.addValue("DPRTID", item.dprtid());
            param.addValue("DPRT_NAME", item.dprtName());
            param.addValue("RGST_DATE", item.rgstDate());
            param.addValue("LAST_DATE", item.lastDate());
            param.addValue("REQ_DATE", item.reqDate());
            param.addValue("TITLE", item.title());
            param.addValue("DOC_TYPE", "CLONE");
            param.addValue("FILE_CNT", item.fileCnt());
            param.addValue("FILE_EXT", item.fileExt());
            param.addValue("SHARE_YN", item.shareYn());
            param.addValue("DEL_YN", item.delYn());
            param.addValue("SAVE_PATH", item.savePath());
            param.addValue("WEB_HTML", item.webHtml());
            param.addValue("FIELD_VALUES", item.fieldValues());
            param.addValue("DOCNO", item.docno());
            param.addValue("EXPR_MONTH", item.exprMonth());
            param.addValue("SCRT_TYPE", item.scrtType());
            param.addValue("SIGN_TYPE", item.signType());
            param.addValue("SIGN_SAVE_PATH", item.signSavePath());
            param.addValue("TRNS_SRC", "TRNS_SUNJIN_APPR");
            param.addValue("TRNS_KEY", item.trnsKey());
            param.addValue("CTGRID", item.ctgrid());
            param.addValue("CTGR_DPRTID", item.ctgrDprtid());
            param.addValue("CTGR_DPRT_NAME", item.ctgrDprtName());
            param.addValue("FORM_OPT", item.formOpt());
            param.addValue("TENANTID", item.tenantid());

            return param;
        } catch (Exception e) {
        log.error("Error processing itemid: {}", item.itemid(), e);
        throw e;
        }
    }
}

