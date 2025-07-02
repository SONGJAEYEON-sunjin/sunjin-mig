package com.kcube.trns.sunjin.cache.docitem;

import com.kcube.trns.sunjin.cache.apitem.DocItemKeyCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocItemCacheTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;
    private final DocItemKeyCache cache;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String apItemQuery = "select apprid, itemid from doc_item where trns_src  = 'TRNS_SUNJIN_APPR' ";

        AtomicInteger cnt = new AtomicInteger();

        jdbcTemplate.query(apItemQuery, rs -> {
            cnt.getAndIncrement();
            String trnsKey = rs.getString("apprid"); // 변경!
            Long itemId = rs.getLong("itemId");
            cache.putApToDocCache(trnsKey, itemId);
        });

        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> docItemCache size : {} ", cnt);

        return RepeatStatus.FINISHED;
    }
}