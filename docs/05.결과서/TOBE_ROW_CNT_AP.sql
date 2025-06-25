select 'AP_ITEM' as table_name , count(*) cnt from AP_ITEM where trns_key is not null
union all
select 'AP_ITEM_SCRT' as table_name , count(*) cnt
from AP_ITEM_SCRT S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_SRCH' as table_name , count(*) cnt
from AP_ITEM_SRCH S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_LINE' as table_name , count(*) cnt
from AP_ITEM_LINE S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null -- 1969833
union all
select 'AP_ITEM_OPN' as table_name , count(*) cnt
from AP_ITEM_OPN S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_SHARE' as table_name , count(*) cnt
from AP_ITEM_SHARE S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_RCVR' as table_name , count(*) cnt
from AP_ITEM_RCVR S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_FILE' as table_name , count(*) cnt
from AP_ITEM_FILE S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'AP_ITEM_RFRN' as table_name , count(*) cnt
from AP_ITEM_RFRN S join AP_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null;
