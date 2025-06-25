package com.kcube.trns.sunjin.cache.orguser;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrgUserInfoRowMapper implements RowMapper<OrgUserInfo> {
    @Override
    public OrgUserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OrgUserInfo(
                rs.getLong("userid"),
                rs.getLong("deptid"),
                rs.getString("namebase")
        );
    }
}
