package com.kcube.trns.sunjin.cache.folder;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FolderCache {

    private final Map<Long, FolderInfo> folderCacheByKmId = new ConcurrentHashMap<Long,FolderInfo>();
    private final Map<String,FolderInfo>  folderCacheByTrnsKey = new ConcurrentHashMap<String,FolderInfo>();


    public void putFolderCache(long kmId, FolderInfo folderInfo) {
        folderCacheByKmId.put(kmId, folderInfo);
        if(folderInfo.trnsKey() != null){
            folderCacheByTrnsKey.put(folderInfo.trnsKey(), folderInfo);
        }
    }

    public FolderInfo getFolderCache(long kmId) {
        return folderCacheByKmId.get(kmId);
    }

    public FolderInfo getFolderCacheByTrnsKey(String key) {
        return folderCacheByTrnsKey.get(key);
    }
}
