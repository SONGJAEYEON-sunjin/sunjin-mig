package com.kcube.trns.sunjin.cache.folder;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FolderRowMapper implements RowMapper<FolderInfo> {

    @Override
    public FolderInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FolderInfo(
                rs.getLong("kmid"),
                rs.getString("name"),
                rs.getString("trns_src"),
                rs.getString("trns_key")
        );
    }
}
