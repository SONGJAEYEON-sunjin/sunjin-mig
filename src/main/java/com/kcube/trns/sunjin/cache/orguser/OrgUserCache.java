package com.kcube.trns.sunjin.cache.orguser;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrgUserCache{
    private final Map<Long, OrgUserInfo> orgUserCacheByUserId = new ConcurrentHashMap<Long, OrgUserInfo>();

    public void putOrgUserCache(long userId, OrgUserInfo orgUserInfo) {
        orgUserCacheByUserId.put(userId, orgUserInfo);
    }

    public OrgUserInfo getOrgUserCache(long userId) {
        return orgUserCacheByUserId.get(userId);
    }
}
