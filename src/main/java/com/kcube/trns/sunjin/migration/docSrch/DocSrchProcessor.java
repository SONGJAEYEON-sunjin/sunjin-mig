package com.kcube.trns.sunjin.migration.docSrch;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DocSrchProcessor implements ItemProcessor<DocSrchReader.DocSrchDto, DocSrchProcessor.DocSrchRow> {

    @Value("${migration.tenant-id}")
    private Long tenantId;

    @Override
    public DocSrchRow process(DocSrchReader.DocSrchDto dto) {

        String contentText = Jsoup
                .parse(dto.webHtml())
                .text();

        if(dto.itemId() % 100 == 0 ){
            log.info(">>>>>>>>>>>>>>>>>>>>>>>> to.itemId() : {} ",dto.itemId());
        }

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



