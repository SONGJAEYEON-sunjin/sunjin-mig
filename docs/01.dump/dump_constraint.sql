/***************************************
  덤프 테이블 제약조건 추가
 ****************************************/
ALTER TABLE DP_ACC_Dept ADD PRIMARY KEY (DeptID);
ALTER TABLE DP_APP_Folder ADD PRIMARY KEY (FolderID);
ALTER TABLE DP_APP_FolderAuth ADD PRIMARY KEY (FolderAuthID, FolderID, DeptID);
ALTER TABLE DP_APP_FolderViewAuth ADD PRIMARY KEY (FolderViewAuthorityID);
ALTER TABLE DP_APP_AccessGrade ADD PRIMARY KEY (AccessID);
ALTER TABLE DP_APP_Doc ADD PRIMARY KEY (DocumentID);
ALTER TABLE DP_APP_CirDoc ADD PRIMARY KEY (CirculationDocumentID);
ALTER TABLE DP_APP_CirAttachFile ADD PRIMARY KEY (CirculationDocumentID, CirculationAttachFileID);
ALTER TABLE DP_APP_CircUser ADD PRIMARY KEY (CirculationDocumentID, CirculationUserID);
ALTER TABLE DP_APP_ForwardDoc ADD PRIMARY KEY (DocumentID, ForwardDocumentID);
ALTER TABLE DP_APP_ForwardUser ADD PRIMARY KEY (DocumentID, ForwardDocumentID, ReceiveUserID);
ALTER TABLE DP_APP_CarbonCopyUser ADD PRIMARY KEY (DocumentID, UserID, DeptID);
ALTER TABLE DP_APP_CarbonCopyUserGroup ADD PRIMARY KEY (DocumentID, UserID, DeptID, ApprovalTag);
ALTER TABLE DP_APP_CounterPartUser ADD PRIMARY KEY (DocumentID, UserID, DeptID, `Sequence`);
ALTER TABLE DP_APP_CounterPartUserModify ADD PRIMARY KEY (ModifySeq, DocumentID);
ALTER TABLE DP_APP_Attach ADD PRIMARY KEY (DocumentID, AttachFileID);
ALTER TABLE DP_APP_BaseDoc ADD PRIMARY KEY (BaseDocumentID, DocumentID);
ALTER TABLE DP_APP_SeqBackup ADD PRIMARY KEY (DocumentID, `Sequence`, ApprovalTag);
ALTER TABLE DP_APP_SeqLater ADD PRIMARY KEY (SeqLaterID, DocumentID, `Sequence`, ApprovalTag);
ALTER TABLE DP_APP_ShortReply ADD PRIMARY KEY (DocumentID, ShortReplyID);