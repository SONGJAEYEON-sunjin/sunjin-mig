package com.kcube.trns.sunjin.migration.docline;

import java.time.LocalDateTime;

public record DocLineRow(
        Long documentId,
        Integer sequence,
        Long userId,
        String nameBase,
        LocalDateTime viewingTime,
        LocalDateTime approvalDate,
        String approvalTag,
        String approvalType,
        String proxy,
        Integer proxyUserId,
        String proxyNameBase
) {}
