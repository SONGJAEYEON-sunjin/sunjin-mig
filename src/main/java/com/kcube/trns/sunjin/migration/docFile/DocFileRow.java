package com.kcube.trns.sunjin.migration.docFile;

public record DocFileRow (
        Long itemId,
        String fileName,
        Long fileSize,
        String filePath,
        String fileGuid
){
}
