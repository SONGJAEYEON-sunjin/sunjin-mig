/***************************************
  인덱스 추가
 ****************************************/


CREATE INDEX idx_approval_document ON dp_app_doc (approvalState, documentId);
CREATE INDEX idx_cirdoc_circulationid ON dp_app_cirdoc (circulationdocumentid);
CREATE INDEX idx_cirdocprod_documentid ON dp_app_cirdoc (documentid);
CREATE INDEX idx_basedoc_parentdocumentid ON dp_app_basedoc(parentdocumentid);

ANALYZE TABLE DP_ACC_User;
ANALYZE TABLE DP_ACC_UserDept;
ANALYZE TABLE DP_ACC_Dept;
ANALYZE TABLE DP_APP_Folder;
ANALYZE TABLE DP_APP_FolderAuth;
ANALYZE TABLE DP_APP_FolderViewAuth;
ANALYZE TABLE DP_APP_AccessGrade;
ANALYZE TABLE DP_APP_Doc;
ANALYZE TABLE DP_APP_CirDoc;
ANALYZE TABLE DP_APP_CirBaseDoc;
ANALYZE TABLE DP_APP_CirAttachFile;
ANALYZE TABLE DP_APP_CircUser;
ANALYZE TABLE DP_APP_ForwardDoc;
ANALYZE TABLE DP_APP_ForwardUser;
ANALYZE TABLE DP_APP_CarbonCopyUser;
ANALYZE TABLE DP_APP_CarbonCopyUserGroup;
ANALYZE TABLE DP_APP_CounterPartUser;
ANALYZE TABLE DP_APP_CounterPartUserModify;
ANALYZE TABLE DP_APP_Attach;
ANALYZE TABLE DP_APP_BaseDoc;
ANALYZE TABLE DP_APP_SeqBackup;
ANALYZE TABLE DP_APP_SeqLater;
ANALYZE TABLE DP_APP_ShortReply;
