package com.kcube.trns.sunjin.cache.docdetail;

public record DocDetail(
        Long documentId,
        int fileCnt,
        String fileExt,
        int rcvCnt,
        String rcvYn,
        String shareYn
) {
}