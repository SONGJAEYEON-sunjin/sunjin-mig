

/**
  데이터 덤프
  (1) 기존테이블 truncate (필요시 실행)
  (2) DBeaver task를 통해 data dump (doc 테이블)
  (3) DBeaver task를 통해 data dump (doc 이외의 테이블)
  (4) 제약조건 및 인덱스 생성 (필요시 실행)
  ====
  (5) 이관용 테이블 생성
 */

-- 2025.06.23 dump
select max(documentid) from dp_app_doc where WriteTimeYMD < '20250101'; -- 1510525 (덤프 범위 확인)

-- dp_app_doc는 테이블 용량이 크기 때문에 쪼개서 dump 진행

-- dp_app_cirdoc는 테이블 용량이 크기 때문에 쪼개서 dump 진행


select count(*) from dp_app_cirdoc where documentid < 1510526; -- 113166
select count(*) from DP_APP_CirBaseDoc where documentid < 1510526; -- 22497
select count(*) from DP_APP_CirAttachFile where documentid < 1510526; -- 130588
select count(*) from DP_APP_CircUser where documentid < 1510526; -- 135903
select count(*) from DP_APP_ForwardDoc where documentid < 1510526; -- 33814
select count(*) from DP_APP_ForwardUser where documentid < 1510526; -- 43529
select count(*) from DP_APP_CarbonCopyUser where documentid < 1510526; -- 1768911
select count(*) from DP_APP_CarbonCopyUserGroup where documentid < 1510526; -- 123409
select count(*) from DP_APP_CounterPartUser where documentid < 1510526; -- 454166
select count(*) from DP_APP_CounterPartUserModify  where documentid < 1510526; -- 3687
select count(*) from DP_APP_Attach  where documentid < 1510526; -- 971356
select count(*) from DP_APP_BaseDoc  where documentid < 1510526; -- 222675
select count(*) from DP_APP_SeqBackup  where documentid < 1510526; -- 3818270
select count(*) from DP_APP_SeqLater  where documentid < 1510526; -- 598
select count(*) from DP_APP_ShortReply  where documentid < 1510526; -- 363426








