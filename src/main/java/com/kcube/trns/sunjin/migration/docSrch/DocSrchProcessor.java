package com.kcube.trns.sunjin.migration.docSrch;

import org.jsoup.Jsoup;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DocSrchProcessor implements ItemProcessor<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow> {

    @Value("${migration.tenant-id}")
    private Long tenantId;

    @Override
    public DocSrchRow process(DocSrchReader.DocSrchDto dto) {

        String contentText = Jsoup
                .parse(dto.webHtml())
                .text();

        return new DocSrchRow(
                dto.itemId(),
                contentText,
                tenantId
        );
    }
    public record DocSrchRow(
            Long itemId,
            String content,
            Long tenantId) {

    }
}



