/***************************************
  이관용 테이블 생성 스크립트
 ****************************************/

-- CREATE TABLE doc_detail (
--      documentid BIGINT PRIMARY KEY,
--      share_yn CHAR(1),
--      rcv_cnt INT,
--      rcv_yn CHAR(1),
--      file_ext VARCHAR(20),
--      file_cnt INT
-- );

-- drop table doc_detail;
-- truncate table doc_detail;

select * from doc_detail;

INSERT INTO doc_detail (
    documentid, share_yn, rcv_cnt, rcv_yn, file_ext, file_cnt
)
SELECT
    doc.documentid AS documentid,
    IF(IFNULL(cd.cnt, 0) + IFNULL(fd.cnt, 0) > 0, 'Y', 'N') AS share_yn,
    IFNULL(cpu.cnt, 0) AS rcv_cnt,
    IF(IFNULL(cpu.cnt, 0) > 0, 'Y', 'N') AS rcv_yn,
    LEFT(
            CASE
                WHEN a.filename IS NULL THEN NULL
                WHEN a.filename NOT LIKE '%.%' THEN NULL
                ELSE SUBSTRING_INDEX(a.filename, '.', -1)
                END, 10
    ) AS file_ext,
    IFNULL(min_attach.cnt, 0) AS file_cnt
FROM dp_app_doc doc
         LEFT JOIN (
    SELECT documentid, COUNT(*) AS cnt FROM DP_APP_CirDoc GROUP BY documentid
) cd ON cd.documentid = doc.documentid
         LEFT JOIN (
    SELECT documentid, COUNT(*) AS cnt FROM DP_APP_ForwardDoc GROUP BY documentid
) fd ON fd.documentid = doc.documentid
         LEFT JOIN (
    SELECT documentid, COUNT(*) AS cnt
    FROM DP_APP_CounterPartUser
    WHERE SUBSTRING(ApprovalTag, 1, 2) = 'ZP'
    GROUP BY documentid
) cpu ON cpu.documentid = doc.documentid
         LEFT JOIN (
    SELECT
        a.documentid AS documentid,
        a.filename AS filename
    FROM dp_app_attach a
             JOIN (
        SELECT documentid, MIN(attachfileid) AS min_attachfileid
        FROM dp_app_attach
        GROUP BY documentid
    ) min_a ON a.documentid = min_a.documentid
        AND a.attachfileid = min_a.min_attachfileid
) a ON a.documentid = doc.documentid
         LEFT JOIN (
    SELECT documentid AS documentid, COUNT(*) AS cnt
    FROM dp_app_attach
    GROUP BY documentid
) min_attach ON min_attach.documentid = doc.documentid
where doc.approvalstate = 'C';

select count(*) from dp_app_doc where approvalstate = 'C';