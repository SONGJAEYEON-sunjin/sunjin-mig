package com.kcube.trns.sunjin.migration.doc;

import com.kcube.trns.sunjin.cache.MigrationCache;
import com.kcube.trns.sunjin.cache.docdetail.DocDetail;
import com.kcube.trns.sunjin.cache.folder.DeptCodeInfo;
import com.kcube.trns.sunjin.cache.form.FormMappingInfo;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocProcessor implements ItemProcessor<DocRow, MapSqlParameterSource> {

    private final MigrationCache cache;

    @Value("${migration.tenant-id}")
    private Long tenantId;

    @Value("${migration.default-userid}")
    private Long defaultUserId;

    @Value("${migration.default-kmid}")
    private Long defaultKmid;

    @Value("${migration.form-id-A}")
    private Long formId_A;

    @Value("${migration.form-id-B}")
    private Long formId_B;

    @Value("${migration.ctgrId}")
    private Long ctgrId;

    @Override
    public MapSqlParameterSource process(DocRow item) throws Exception {

        UserInfo user = cache.getUserInfoByTrnsKey(String.valueOf(item.userId()));

        MapSqlParameterSource params = new MapSqlParameterSource();

        DocDetail docDetail = cache.getDocDetail(item.documentId());

        if(item.documentId() % 100 == 0){
            log.info(">>>>>>>>>>>>>>>>> item.documentid : {} ",item.documentId());
        }

        if(item.parentDocumentId() > 0){
            DocDetail parentdocDetail = cache.getDocDetail(item.parentDocumentId());
            if(parentdocDetail == null){
                params.addValue("ORGID", null);
            }else{
                params.addValue("ORGID", (parentdocDetail.rcvYn().equals("Y")) ? item.parentDocumentId() : null);
            }
        }else{
            params.addValue("ORGID", null);
        }

        FormMappingInfo formMappingInfo = cache.getFormMappingCache(item.formId());
//        if(formMappingInfo != null){
            params.addValue("FORMID", formMappingInfo.tobeFormId() );
            params.addValue("FORM_NAME", formMappingInfo.tobeFormName());
//        }else{
//            log.info(">>>>>>>>>>>>>>>>> not mapping form item.documentid : {}",item.documentId());
//            params.addValue("FORMID",formId_A);
//            params.addValue("FORM_NAME", "{\"ko\":\"이관양식\",\"en\":\"\",\"zh\":\"\",\"ja\":\"\"}");
//        }

        params.addValue("GRPID", cache.getGrpIdCacheByTrnsKey(item.accessTitleId()));

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
//                params.addValue("DPRT_NAME", "선진");
        }else{
            params.addValue("USERID", user.userId());
            params.addValue("USER_NAME", user.name());
            params.addValue("USER_DISP", user.userDisp());
            params.addValue("DPRTID", user.dprtId());
//            params.addValue("DPRT_NAME", user.dprtName());
        }

        DeptCodeInfo deptCodeInfo = cache.getDeptCode(item.deptCodeId());
        if(deptCodeInfo == null || deptCodeInfo.deptId() == null){
            params.addValue("DPRTID", defaultKmid);
            params.addValue("DPRT_NAME", "선진");
            params.addValue("CTGR_DPRTID", defaultKmid);
            params.addValue("CTGR_DPRT_NAME", "선진");
        }else{
            params.addValue("DPRTID", deptCodeInfo.kmId());
            params.addValue("DPRT_NAME", deptCodeInfo.name());
            params.addValue("CTGR_DPRTID", deptCodeInfo.kmId());
            params.addValue("CTGR_DPRT_NAME", deptCodeInfo.name());
        }

        params.addValue("RGST_DATE", item.writeTime());
        params.addValue("LAST_DATE", item.writeTime());
        params.addValue("REQ_DATE", item.writeTime());
        params.addValue("COMP_DATE", item.completeDate());

        String title = StringEscapeUtils.unescapeHtml4(item.subject());
        params.addValue("TITLE", title);

        params.addValue("STATUS", "END");

        if (docDetail != null) {
            params.addValue("FILE_CNT", docDetail.fileCnt());
            params.addValue("FILE_EXT", docDetail.fileExt());
            params.addValue("RCV_CNT", docDetail.rcvCnt());
            params.addValue("RCV_YN", docDetail.rcvYn());
        } else {
            params.addValue("FILE_CNT", 0);
            params.addValue("FILE_EXT", null);
            params.addValue("RCV_CNT", 0);
            params.addValue("RCV_YN", "N");
        }

        params.addValue("SHARE_YN", docDetail.shareYn());
        params.addValue("HIDE_YN", "N");
        params.addValue("DEL_YN", "N");

        params.addValue("WEB_HTML", DocHtmlTrnsUtil.transform(item.contentHTML()));
        params.addValue("FIELD_VALUES", "{}");

        String docno = StringEscapeUtils.unescapeHtml4(item.docNumber());
        params.addValue("DOCNO", docno);

        params.addValue("EXPR_MONTH", -1);
        params.addValue("SCRT_TYPE", "DEPT");
        params.addValue("SIGN_TYPE", "ALL");
        params.addValue("TENANTID", tenantId);
        params.addValue("TRNS_SRC", "TRNS_SUNJIN_APPR");
        params.addValue("TRNS_KEY", item.documentId());
        params.addValue("SYNCID", null);
        params.addValue("CTGRID", ctgrId);
        params.addValue("LDGR_ACTN_YN", "N");
        params.addValue("LDGR_SEND_YN", "N");
        params.addValue("FAKE_YN", "N");

        return params;
    }
}



