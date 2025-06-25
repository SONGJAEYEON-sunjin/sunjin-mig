package com.kcube.trns.sunjin.cache.grp;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GrpInfoCache {
    private final Map<String,Long> grpIdCacheByTrnsKey = new ConcurrentHashMap<String,Long>();

    public void putGrpIdCache(String trnsKey, Long kmId) {
        grpIdCacheByTrnsKey.put(trnsKey, kmId);
    }

    public Long getGrpIdCacheByTrnsKey(String key) {
        return grpIdCacheByTrnsKey.get(key);
    }
}
