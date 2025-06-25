package com.kcube.trns.sunjin.migration.todoc;

import java.time.LocalDateTime;

public record ApItem(
        Long orgid,
        Long itemid,
        Long formid,
        String formName,
        Long grpid,
        Long userid,
        String userName,
        String userDisp,
        Long dprtid,
        String dprtName,
        LocalDateTime rgstDate,
        LocalDateTime lastDate,
        LocalDateTime reqDate,
        String title,
        Integer fileCnt,
        String fileExt,
        String shareYn,
        String delYn,
        String savePath,
        String webHtml,
        String fieldValues,
        String docno,
        Integer exprMonth,
        String scrtType,
        String signType,
        String signSavePath,
        String ctgrid,
        Long ctgrDprtid,
        String ctgrDprtName,
        String formOpt,
        String mContent,
        Long tenantid,
        String trnsKey
) {}


