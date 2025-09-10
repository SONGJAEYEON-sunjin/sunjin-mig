

/**
  데이터 덤프
  (1) 기존테이블 truncate (필요시 실행)
  (2) DBeaver task를 통해 data dump (doc 테이블)
  (3) DBeaver task를 통해 data dump (doc 이외의 테이블)
  (4) 제약조건 및 인덱스 생성 (필요시 실행)
  ====
  (5) 이관용 테이블 생성
 */

truncate table doc_detail;
truncate table DP_ACC_User;
truncate table DP_ACC_UserDept;
truncate table DP_ACC_Dept;
truncate table DP_COM_DeptCode;
truncate table DP_APP_Folder; 
truncate table DP_APP_FolderAuth; 
truncate table DP_APP_FolderViewAuth;
truncate table DP_APP_AccessGrade;  
truncate table DP_APP_Doc ; 
truncate table dp_app_cirdoc; 
truncate table DP_APP_CirBaseDoc; 
truncate table DP_APP_CirAttachFile ; 
truncate table DP_APP_CircUser ; 
truncate table DP_APP_ForwardDoc ; 
truncate table DP_APP_ForwardUser ; 
truncate table DP_APP_CarbonCopyUser ; 
truncate table DP_APP_CarbonCopyUserGroup ;
truncate table DP_APP_CounterPartUser ; 
truncate table DP_APP_CounterPartUserModify  ; 
truncate table DP_APP_Attach  ; 
truncate table DP_APP_BaseDoc  ; 
truncate table DP_APP_SeqBackup  ; 
truncate table DP_APP_SeqLater  ; 
truncate table DP_APP_ShortReply  ;


-- 2025.06.23 dump
select max(documentid) from dp_app_doc where WriteTimeYMD < '20250101'; -- 1510525 (덤프 범위 확인)

-- dp_app_doc는 테이블 용량이 크기 때문에 쪼개서 dump 진행
SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 0 AND 30000; -- 21306
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 0 AND 30000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 30001 AND 60000; -- 20849
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 30001 AND 60000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 60001 AND 90000; -- 20989
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 60001 AND 90000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 90001 AND 120000; -- 20889
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 90001 AND 120000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 120001 AND 150000; -- 21567
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 120001 AND 150000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 150001 AND 180000; -- 24243
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 150001 AND 180000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 180001 AND 210000; --  24820
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 180001 AND 210000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 210001 AND 240000; -- 25269
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 210001 AND 240000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 240001 AND 270000; -- 25582
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 240001 AND 270000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 270001 AND 300000; -- 25653
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 270001 AND 300000 order BY documentid;

-- ================================================ 1 

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 300001 AND 330000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 300001 AND 330000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 330001 AND 360000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 330001 AND 360000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 360001 AND 390000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 360001 AND 390000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 390001 AND 420000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 390001 AND 420000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 420001 AND 450000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 420001 AND 450000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 450001 AND 480000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 450001 AND 480000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 480001 AND 510000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 480001 AND 510000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 510001 AND 540000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 510001 AND 540000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 540001 AND 570000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 540001 AND 570000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 570001 AND 600000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 570001 AND 600000 order BY documentid;

-- ================================================ 2 

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 600001 AND 630000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 600001 AND 630000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 630001 AND 660000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 630001 AND 660000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 660001 AND 690000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 660001 AND 690000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 690001 AND 720000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 690001 AND 720000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 720001 AND 750000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 720001 AND 750000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 750001 AND 780000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 750001 AND 780000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 780001 AND 810000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 780001 AND 810000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 810001 AND 840000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 810001 AND 840000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 840001 AND 870000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 840001 AND 870000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 870001 AND 900000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 870001 AND 900000 order BY documentid;

-- ================================================ 3 

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 900001 AND 930000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 900001 AND 930000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 930001 AND 960000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 930001 AND 960000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 960001 AND 990000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 960001 AND 990000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 990001 AND 1020000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 990001 AND 1020000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1020001 AND 1050000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1020001 AND 1050000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1050001 AND 1080000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1050001 AND 1080000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1080001 AND 1110000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1080001 AND 1110000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1110001 AND 1140000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1110001 AND 1140000 order BY documentid;


SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1140001 AND 1170000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1140001 AND 1170000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1170001 AND 1200000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1170001 AND 1200000 order BY documentid;

-- ================================================ 4 

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1200001 AND 1230000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1200001 AND 1230000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1230001 AND 1260000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1230001 AND 1260000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1260001 AND 1290000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1260001 AND 1290000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1290001 AND 1320000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1290001 AND 1320000 order BY documentid;
          
SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1320001 AND 1350000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1320001 AND 1350000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1350001 AND 1380000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1350001 AND 1380000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1380001 AND 1410000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1380001 AND 1410000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1410001 AND 1440000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1410001 AND 1440000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1440001 AND 1470000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1440001 AND 1470000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1470001 AND 1500000;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1470001 AND 1500000 order BY documentid;

SELECT COUNT(*) FROM dp_app_doc WHERE DocumentID BETWEEN 1500001 AND 1510525;
SELECT * FROM dp_app_doc WHERE DocumentID BETWEEN 1500001 AND 1510525 order BY documentid;


-- dp_app_cirdoc는 테이블 용량이 크기 때문에 쪼개서 dump 진행
select * from DP_APP_CirDoc where DocumentID BETWEEN 0 AND 500000 order by documentid;
select * from DP_APP_CirDoc where DocumentID BETWEEN 500001 AND 1000000 order by documentid;
select * from DP_APP_CirDoc where DocumentID BETWEEN 1000001 AND 1510525 order by documentid;

select * from DP_ACC_User; -- 2855 각 테이블 dump count 확인
select * from DP_ACC_UserDept; -- 2891
select * from DP_ACC_Dept; -- 909
select * from DP_COM_DeptCode;
select * from DP_APP_Folder; -- 17
select * from DP_APP_FolderAuth;  -- 667
select * from DP_APP_FolderViewAuth; -- 2705
select * from DP_APP_AccessGrade;  -- 4

select * from DP_APP_Doc where documentid between 1510526 and 1525131 order by CompanyId,DocumentID;
select * from dp_app_cirdoc where documentid between 1510526 and 1525131 order by CirculationDocumentID;
select * from DP_APP_CirBaseDoc where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_CirAttachFile where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_CircUser where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_ForwardDoc where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_ForwardUser where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_CarbonCopyUser where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_CarbonCopyUserGroup where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_CounterPartUser where documentid between 1510526 and 1525131 order by DocumentID, UserID, DeptID, Sequence;
select * from DP_APP_CounterPartUserModify  where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_Attach  where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_BaseDoc  where documentid between 1510526 and 1525131 order by DocumentID;
select * from DP_APP_SeqBackup  where documentid between 1510526 and 1525131 order by DocumentID,Sequence,ApprovalTag;
select * from DP_APP_SeqLater  where documentid between 1510526 and 1525131 order by DocumentID,Sequence,ApprovalTag;
select * from DP_APP_ShortReply  where documentid between 1510526 and 1525131 order by shortreplyid,DocumentID;

select count(*) from DP_APP_Doc where documentid between 1510526 and 1525131 order by CompanyId,DocumentID;
select count(*) from dp_app_cirdoc where documentid between 1510526 and 1525131 order by CirculationDocumentID;
select count(*) from DP_APP_CirBaseDoc where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_CirAttachFile where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_CircUser where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_ForwardDoc where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_ForwardUser where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_CarbonCopyUser where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_CarbonCopyUserGroup where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_CounterPartUser where documentid between 1510526 and 1525131 order by DocumentID, UserID, DeptID, Sequence;
select count(*) from DP_APP_CounterPartUserModify  where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_Attach  where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_BaseDoc  where documentid between 1510526 and 1525131 order by DocumentID;
select count(*) from DP_APP_SeqBackup  where documentid between 1510526 and 1525131 order by DocumentID,Sequence,ApprovalTag;
select count(*) from DP_APP_SeqLater  where documentid between 1510526 and 1525131 order by DocumentID,Sequence,ApprovalTag;
select count(*) from DP_APP_ShortReply  where documentid between 1510526 and 1525131 order by shortreplyid,DocumentID;

-- =====================================
-- =======  doc_detail insert ==========
-- ===================================== 

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






