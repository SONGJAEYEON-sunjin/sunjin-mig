package com.kcube.trns.sunjin.cache.folder;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeptCodeRowMapper implements RowMapper<DeptCodeInfo> {
    @Override
    public DeptCodeInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DeptCodeInfo(
                rs.getLong("deptcodeid"),
                rs.getLong("deptid"),
                rs.getLong("kmid"),
                rs.getString("name")
        );
    }
}
