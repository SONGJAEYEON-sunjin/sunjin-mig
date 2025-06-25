package com.kcube.trns.sunjin.cache.docdetail;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DocDetailCache {

    private final Map<Long, DocDetail> docDetailByDocId = new ConcurrentHashMap<Long,DocDetail>();

    public void putDocDetailCache(long docId, DocDetail docInfo) {
        docDetailByDocId.put(docId, docInfo);
    }

    public DocDetail getDocDetail(long docId) {
        return docDetailByDocId.get(docId);
    }
}
