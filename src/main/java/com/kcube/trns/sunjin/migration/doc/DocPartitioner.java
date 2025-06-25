package com.kcube.trns.sunjin.migration.doc;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@StepScope
@RequiredArgsConstructor
@AllArgsConstructor
public class DocPartitioner implements Partitioner {

    private Long minId;
    private Long maxId;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (minId == null || maxId == null || minId > maxId) {
            throw new IllegalStateException("유효한 documentid 범위를 찾을 수 없습니다.");
        }

        long range = (maxId - minId) / gridSize;
        Map<String, ExecutionContext> result = new HashMap<>();
        long start = minId;

        for (int i = 0; i < gridSize; i++) {
            long end = (i == gridSize - 1) ? maxId : start + range;
            ExecutionContext context = new ExecutionContext();
            context.putLong("minId", start);
            context.putLong("maxId", end);
            result.put("partition" + i, context);
            start = end + 1;
        }

        return result;
    }
}

