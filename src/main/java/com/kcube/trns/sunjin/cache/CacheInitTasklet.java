package com.kcube.trns.sunjin.cache;

import com.kcube.trns.sunjin.cache.docdetail.DocDetail;
import com.kcube.trns.sunjin.cache.docdetail.DocDetailRowMapper;
import com.kcube.trns.sunjin.cache.folder.FolderInfo;
import com.kcube.trns.sunjin.cache.folder.FolderRowMapper;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import com.kcube.trns.sunjin.cache.user.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInitTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final MigrationCache cache;

    @Value("${migration.tenant-id}")
    private String tenantId;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        // folerCache 초기화
        String folerQuery = "SELECT kmid, name, trns_src, trns_key FROM km WHERE tenantId = ? ORDER BY kmid";
        List<FolderInfo> folderInfoList = jdbcTemplate.query(folerQuery, new FolderRowMapper(), tenantId);

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> folerCache.size : {} ",folderInfoList.size());

        for(FolderInfo folderInfo : folderInfoList){
            if (folderInfo.kmId() == null) {
                log.error("⚠️ FolderInfo with null kmId: {}", folderInfo);
                continue;
            }
            cache.putFolderCache(folderInfo.kmId(), folderInfo);
        }

        // userCache 초기화
        String userQuery = "SELECT userid, name, user_disp, dprtid, dprt_name, pstnId, pstn_name, gradeid, grade_name, trns_src, trns_key FROM hr_user WHERE tenantid = ? ORDER BY userid";
        List<UserInfo> userInfoList = jdbcTemplate.query(userQuery, new Object[]{tenantId},new UserRowMapper());

        for(UserInfo userInfo : userInfoList){
            cache.putUserCache(userInfo.userId(), userInfo);
        }
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> userCache.size : {} ", userInfoList.size());

        // docDetailCache 초기화

        String docDetailQuery = "select documentid, file_cnt, file_ext, rcv_cnt, rcv_yn,share_yn from doc_detail order by documentid";

        List<DocDetail> docDetailList = jdbcTemplate.query(docDetailQuery, new DocDetailRowMapper());

        for(DocDetail docInfo : docDetailList){
            cache.putDocDetailCache(docInfo.documentId(), docInfo);
        }

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> docDetailCache .size : {} ", docDetailList.size());

        // grpid cache 초기화
        String grpidQuery = "select kmid,trns_key from folder_km where trns_src = 'SUNJIN_DP_APP_SCRT'  ";

        AtomicInteger grpidQueryCnt = new AtomicInteger();
        jdbcTemplate.query(grpidQuery, rs -> {
            grpidQueryCnt.getAndIncrement();
            String trnsKey = rs.getString("trns_key");
            Long kmid = rs.getLong("kmid");
            cache.putGrpIdCache(trnsKey, kmid);
        });

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> grpidCache size : {} ", grpidQueryCnt);

        // orgUserInfo cache 초기화 (미사용캐시)
//        String orgUserInfoQuery = """
//                select ud.userid ,ud.deptid ,d.namebase
//                from dp_acc_userdept ud join dp_acc_dept d
//                on ud.deptid = d.deptid
//                where userdeptorder = 1
//        """;
//
//        List<OrgUserInfo> orgUserInfoList = jdbcTemplate.query(orgUserInfoQuery, new OrgUserInfoRowMapper());
//
//        for(OrgUserInfo orgUserInfo : orgUserInfoList){
//            cache.putOrgUserCache(orgUserInfo.userId(), orgUserInfo);
//        }
//
//        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> orgUserInfo size : {} ", orgUserInfoList.size());

        return RepeatStatus.FINISHED;
    }
}
