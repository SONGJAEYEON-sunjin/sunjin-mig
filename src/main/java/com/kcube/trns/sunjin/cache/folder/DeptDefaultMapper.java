package com.kcube.trns.sunjin.cache.folder;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeptDefaultMapper implements RowMapper<DeptDefaultInfo> {
    @Override
    public DeptDefaultInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DeptDefaultInfo(
                rs.getLong("asis_deptid"),
                rs.getLong("tobe_dprtid"),
                rs.getString("tobe_dprt_name")
        );
    }
}
