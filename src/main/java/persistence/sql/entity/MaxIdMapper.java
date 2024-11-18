package persistence.sql.entity;

import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MaxIdMapper implements RowMapper {
    @Override
    public Long mapRow(final ResultSet resultSet) throws SQLException {
        return resultSet.getLong(1);
    }
}
