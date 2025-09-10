package com.kcube.trns.sunjin.cache.form;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FormMappingInfoMapper implements RowMapper<FormMappingInfo> {
    @Override
    public FormMappingInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FormMappingInfo(
                rs.getLong("asis_formid"),
                rs.getLong("tobe_formid"),
                rs.getString("tobe_form_name")
        );
    }
}
