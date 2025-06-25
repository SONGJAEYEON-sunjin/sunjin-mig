select 'DP_APP_DOC' as table_name , count(*) cnt  from DP_APP_DOC where approvalstate = 'C'-- 
union all
select 'DP_APP_DOC' as table_name , count(*) cnt  from DP_APP_DOC where approvalstate = 'C'--
union all
select 'D_APP_DOC' as table_name , count(*) cnt  from DP_APP_DOC where approvalstate = 'C' --
union all
select 'DP_APP_SeqBackup' as table_name , count(*) cnt
from DP_APP_DOC m join DP_APP_SeqBackup s
                       on m.DocumentID = s.DocumentID
where m.approvalstate = 'C' and s.[Sequence] > 1 -- 1969846
union all
select 'DP_APP_ShortReply' as table_name , count(*) cnt
from DP_APP_DOC m join DP_APP_ShortReply s
                       on m.DocumentID = s.DocumentID
where m.approvalstate = 'C'   -- 233082
union all
select 'Cir/Forward/Carbon' as table_name, sum(cnt)  -- 1380477
FROM (
         SELECT
             'DP_APP_CirDoc' AS table_name, -- 106313
             COUNT(*) AS cnt
         FROM DP_APP_DOC m
                  JOIN DP_APP_CirDoc s
                       ON m.DocumentID = s.DocumentID
                  JOIN dp_app_circuser cu
                       ON s.CirculationDocumentID = cu.CirculationDocumentID
         WHERE m.ApprovalState = 'C'
         union all
         select 'dp_app_forwarduser' as table_name , count(*) cnt -- 32106
         from DP_APP_DOC m join dp_app_forwarduser s
                                on m.DocumentID = s.DocumentID
         where m.approvalstate = 'C'
         union all
         select 'DP_APP_CarbonCopyUser' as table_name , count(*) cnt -- 1242058
         from DP_APP_DOC m join DP_APP_CarbonCopyUser s
                                on m.DocumentID = s.DocumentID
         where m.approvalstate = 'C'
     ) share
union all
select 'DP_APP_CounterPartUser' AS table_name,
       COUNT(*) AS cnt
from DP_APP_CounterPartUser u
         LEFT JOIN (
    SELECT m1.*
    FROM DP_APP_CounterPartUserModify m1
             JOIN (
        SELECT documentid, ApprovalTag, MAX(modifySeq) AS maxSeq
        FROM DP_APP_CounterPartUserModify
        GROUP BY documentid, ApprovalTag
    ) m2
                  ON m1.documentid = m2.documentid
                      AND m1.ApprovalTag = m2.ApprovalTag
                      AND m1.modifySeq = m2.maxSeq
) m ON u.documentid = m.documentid AND u.ApprovalTag = m.ApprovalTag
         JOIN dp_app_doc d ON u.documentid = d.DocumentID
where u.userid > 0
  AND SUBSTRING(u.ApprovalTag, 1, 2) = 'ZP'
  AND d.approvalstate = 'C'
union all
select 'DP_APP_Attach' as table_name , count(*) cnt
from dp_app_doc m join DP_APP_Attach s
                       on m.DocumentID = s.DocumentID
where m.approvalstate = 'C'
union all
select 'DP_APP_BaseDoc' as table_name , count(*) cnt
FROM dp_app_basedoc bd left JOIN dp_app_doc d
                                 ON bd.ParentDocumentID  = d.documentid
where d.approvalState = 'C'; 
