package com.kcube.trns.sunjin.cache.user;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserCache {
    private final Map<Long,UserInfo> userCacheByUserId = new ConcurrentHashMap<Long,UserInfo>();
    private final Map<String,UserInfo> userCacheByTrnsKey = new ConcurrentHashMap<String,UserInfo>();

    public void putUserCache(long userId, UserInfo userInfo) {
        userCacheByUserId.put(userId, userInfo);
        if(userInfo.trnsKey() != null){
            userCacheByTrnsKey.put(userInfo.trnsKey(), userInfo);
        }
    }

    public UserInfo getUserInfo(long userId) {
        return userCacheByUserId.get(userId);
    }

    public UserInfo getUserInfoByTrnsKey(String key) {
        return userCacheByTrnsKey.get(key);
    }
}
