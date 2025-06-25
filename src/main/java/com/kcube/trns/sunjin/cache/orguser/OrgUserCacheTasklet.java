package com.kcube.trns.sunjin.cache.orguser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrgUserCacheTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final OrgUserCache orgUserCache;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // orgUserInfo cache 초기화
        String orgUserInfoQuery = """
                select ud.userid ,ud.deptid ,d.namebase
                from dp_acc_userdept ud join dp_acc_dept d
                on ud.deptid = d.deptid
                where userdeptorder = 1
        """;

        List<OrgUserInfo> orgUserInfoList = jdbcTemplate.query(orgUserInfoQuery, new OrgUserInfoRowMapper());

        for(OrgUserInfo orgUserInfo : orgUserInfoList){
            orgUserCache.putOrgUserCache(orgUserInfo.userId(), orgUserInfo);
        }

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> orgUserInfo size : {} ", orgUserInfoList.size());

        return RepeatStatus.FINISHED;
    }
}