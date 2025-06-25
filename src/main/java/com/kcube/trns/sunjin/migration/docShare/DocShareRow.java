package com.kcube.trns.sunjin.migration.docShare;

import java.time.LocalDateTime;

public record DocShareRow(
        Long documentId,
        Long userId,
        String nameBase,
        LocalDateTime shareDate,
        Long deptid,
        String isGroup,
        int isSubTree
) {
}
