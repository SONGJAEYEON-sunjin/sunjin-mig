package com.kcube.trns.sunjin.migration.docOpn;

import java.time.LocalDateTime;

public record DocOpnRow(
        Long documentId,
        Integer section,
        String content,
        Long userId,
        String nameBase,
        LocalDateTime writeTime,
        Long shortReplyId
) {
}
