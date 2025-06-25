package com.kcube.trns.sunjin.cache.docdetail;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocDetailRowMapper implements RowMapper<DocDetail> {

    @Override
    public DocDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new DocDetail(
                rs.getLong("documentid"),
                rs.getInt("file_cnt"),
                rs.getString("file_ext"),
                rs.getInt("rcv_cnt"),
                rs.getString("rcv_yn"),
                rs.getString("share_yn")
        );
    }
}
