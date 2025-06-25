package com.kcube.trns.sunjin.cache.apitem;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DocItemKeyCache {

    private final Map<String,Long> docItemMapFromAppDocToAp = new ConcurrentHashMap<String,Long>();
    public void putItemIdCache(
            String trnsKey, long itemId) {
        docItemMapFromAppDocToAp.put(trnsKey, itemId);
    }
    public Long getItemIdCacheByTrnsKey(String trnsKey) {
        return docItemMapFromAppDocToAp.get(trnsKey);
    }

    private final Map<String,Long> docItemMapFormApToDoc = new ConcurrentHashMap<String,Long>();
    public void putApToDocCache(String trnsKey, long itemId) {
        docItemMapFormApToDoc.put(trnsKey, itemId);
    }
    public Long getDocIdByTrnskey(String trnsKey) {
        return docItemMapFormApToDoc.get(trnsKey);
     }
}
