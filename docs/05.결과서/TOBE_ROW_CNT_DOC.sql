select 'DOC_ITEM' as table_name , count(*) cnt from DOC_ITEM where trns_key is not null
union all
select 'DOC_ITEM_SCRT' as table_name , count(*) cnt
from DOC_ITEM_SCRT S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_SRCH' as table_name  , count(*) cnt
from DOC_ITEM_SRCH S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_LINE' as table_name, count(*) cnt
from DOC_ITEM_LINE S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_OPN' as table_name , count(*) cnt
from DOC_ITEM_OPN S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_SHARE' as table_name, count(*) cnt
from DOC_ITEM_SHARE S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_RCVR' as table_name, count(*) cnt
from DOC_ITEM_RCVR S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_FILE' as table_name, count(*) cnt
from DOC_ITEM_FILE S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null
union all
select 'DOC_ITEM_RFRN' as table_name , count(*) cnt
from DOC_ITEM_RFRN S join DOC_ITEM M on S.ITEMID = M.ITEMID where M.TRNS_KEY is not null;
