package com.kcube.trns.sunjin.migration.doc;

import java.sql.Timestamp;

public record DocRow (
    Long documentId,
    Long userId,
    Long parentDocumentId,
    String accessTitleId,
    String subject,
    String nameBase,
    Timestamp writeTime,
    Timestamp completeDate,
    String contentHTML,
    String docNumber,
    Long formId
) {
}
