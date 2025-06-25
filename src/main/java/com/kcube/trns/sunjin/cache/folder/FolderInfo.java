package com.kcube.trns.sunjin.cache.folder;

public record FolderInfo (
        Long kmId,
        String name,
        String trnsSrc,
        String trnsKey
){
}
// 별도의 어노테이션 필요 없음
// record가 자체 생성자 getter equals hashcode 자동생성함
// 직렬화 필요하면 implement Serializable 추가가능!~