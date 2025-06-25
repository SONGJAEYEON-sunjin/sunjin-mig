package com.kcube.trns.sunjin.migration.docRcvr;

import java.time.LocalDateTime;

public record DocRcvrRow(
        Long itemId,
        Long acctId,
        String acctYn,
        LocalDateTime acctDate,
        Long userId,
        String nameBase
) {
}
