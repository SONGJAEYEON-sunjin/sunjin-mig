package com.kcube.trns.sunjin.cache.user;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<UserInfo> {

    @Override
    public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserInfo(
                rs.getLong("userid"),
                rs.getString("name"),
                rs.getString("user_disp"),
                rs.getLong("dprtid"),
                rs.getString("dprt_name"),
                rs.getLong("gradeid"),
                rs.getString("grade_name"),
                rs.getLong("pstnId"),
                rs.getString("pstn_name"),
                rs.getString("trns_src"),
                rs.getString("trns_key")
        );
    }
}
