package com.kcube.trns.sunjin.common;

import org.springframework.stereotype.Component;

@Component
public class UserXid {

    private static final long SINGLE = 100_000_000L;
    private static final long DOUBLE = SINGLE * SINGLE;
    private static final int CODE_USER = 1;
    public static final int CODE_BELOW = 4;
    private static final int CODE_EXACT = 5;

    public Long makeUserXid(Long userId)
    {
        return makeXidByCode(CODE_USER, null, userId);
    }

    public Long getExactDprtXid(Long dprtId) {
        return makeXidByCode(CODE_EXACT, dprtId, null);
    }

    public Long makeXidByCode(int code, Long tid, Long lid) {
        long t = (tid == null) ? 0L : tid;
        long l = (lid == null) ? 0L : lid;
        return code * DOUBLE + t * SINGLE + l;
    }

    public Long getBelowDprtXid(Long dprtId)
    {
        return makeXidByCode(CODE_BELOW, dprtId, null);
    }



//    AP_ITEM의 dprtId에 해당하는 17자리 exact 보안코드

//    /**
//     * 부서 id와 직급 id로부터 EXACT xid를 만들어준다.
//     */
//    public static Long makeExactXid(Long dprtId, Long gradeId)
//    {
//        return makeXidByCode(CODE_EXACT, dprtId, gradeId);
//    }

//    // 사용자정보 xid 생성하는 유틸
//    public double getUserXid(long userId){
//        return 0L;
//    }







}
