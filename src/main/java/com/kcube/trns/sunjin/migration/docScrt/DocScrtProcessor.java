package com.kcube.trns.sunjin.migration.docScrt;

import com.kcube.trns.sunjin.common.UserXid;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DocScrtProcessor implements ItemProcessor<DocScrtReader.ApItem, DocScrtProcessor.DocScrtRow> {

    private final UserXid userXid;

    @Override
    public DocScrtRow process(DocScrtReader.ApItem item) {
        Long groupXid = userXid.getExactDprtXid(item.dprtId());
        return new DocScrtRow(
                item.itemId(),
                groupXid,
                item.dprtName()
        );
    }

    public record DocScrtRow(
            Long itemId,
            Long groupId,
            String groupName
    ) {}
}