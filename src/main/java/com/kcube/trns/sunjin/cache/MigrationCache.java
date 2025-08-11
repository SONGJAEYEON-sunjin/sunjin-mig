package com.kcube.trns.sunjin.cache;

import com.kcube.trns.sunjin.cache.docdetail.DocDetail;
import com.kcube.trns.sunjin.cache.folder.DeptCodeInfo;
import com.kcube.trns.sunjin.cache.folder.FolderInfo;
import com.kcube.trns.sunjin.cache.orguser.OrgUserInfo;
import com.kcube.trns.sunjin.cache.user.UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MigrationCache {

    // folder cache
    private final Map<Long, FolderInfo>  folderCacheByKmId = new ConcurrentHashMap<Long,FolderInfo>();
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

    // deptcode cache
    private final Map<Long, DeptCodeInfo> deptCodeCache = new ConcurrentHashMap<>();

    public void putDeptCodeCache(Long deptCodeId, DeptCodeInfo info) {
        deptCodeCache.put(deptCodeId, info);
    }

    // 이걸로 갖고와서 set 해주면 되겠다,,,
    public DeptCodeInfo getDeptCode(Long deptCodeId) {
        return deptCodeCache.get(deptCodeId);
    }

    // user cache
    private final Map<Long, UserInfo>  userCacheByUserId = new ConcurrentHashMap<Long,UserInfo>();
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

    // attachInfo cache
    private final Map<Long, DocDetail> docDetailByDocId = new ConcurrentHashMap<Long,DocDetail>();

    public void putDocDetailCache(long docId, DocDetail docInfo) {
        docDetailByDocId.put(docId, docInfo);
    }

    public DocDetail getDocDetail(long docId) {
        return docDetailByDocId.get(docId);
    }

    // grdInfo cache
    private final Map<String,Long>  grpIdCacheByTrnsKey = new ConcurrentHashMap<String,Long>();

    public void putGrpIdCache(String trnsKey, Long kmId) {
        grpIdCacheByTrnsKey.put(trnsKey, kmId);
    }

    public Long getGrpIdCacheByTrnsKey(String key) {
        return grpIdCacheByTrnsKey.get(key);
    }

    // orgUserCache
    private final Map<Long, OrgUserInfo> orgUserCacheByUserId = new ConcurrentHashMap<Long, OrgUserInfo>();

    public void putOrgUserCache(long userId, OrgUserInfo orgUserInfo) {
        orgUserCacheByUserId.put(userId, orgUserInfo);
    }

    public OrgUserInfo getOrgUserCache(long userId) {
        return orgUserCacheByUserId.get(userId);
    }
}
