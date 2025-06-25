-- dev

SELECT userid, ' https://intradev.sj.co.kr/DeskPlusEIP/FileData/1/Users/' + CAST(userid AS VARCHAR) + '/Picture/' + sealimage AS file_url
from DP_ACC_User
WHERE LEN(sealimage) > 0
order by userid desc;


-- prod
SELECT userid, Namebase, 'https://intra.sj.co.kr/DeskPlusEIP/FileData/1/Users/' + CAST(userid AS VARCHAR) + '/Picture/' + sealimage AS file_url
from DP_ACC_User
WHERE LEN(sealimage) > 0
order by userid desc;