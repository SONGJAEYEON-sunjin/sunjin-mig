-- user_conf delete 필요시 실행
delete from user_conf u join hr_user h
on h.userid = u.userid
where trns_src='USER1090';

-- user_conf insert 
INSERT INTO user_conf (
    USERID, SIGN_TYPE, SIGN_SAVE_PATH,
    ARRIVE_MAIL_YN, END_MAIL_YN, SCRT_YN, AGNT_YN, HOLD_HIDE_YN
)
select
    h.userid AS USERID,
    IF(LENGTH(u.Sealimage) > 0, 'IMAGE', 'TEXT') AS SIGN_TYPE,
    IF(LENGTH(u.Sealimage) > 0, CONCAT('SealImage/', u.SealImage), NULL) AS SIGN_SAVE_PATH,
    'Y' AS ARRIVE_MAIL_YN,
    'Y' AS END_MAIL_YN,
    'N' AS SCRT_YN,
    'N' AS AGNT_YN,
    'N' AS HOLD_HIDE_YN
FROM dp_acc_user u
JOIN hr_user h
ON h.trns_key = u.UserID
WHERE h.tenantid = 1090 and trns_src = 'USER1090'
order by u.userid;
