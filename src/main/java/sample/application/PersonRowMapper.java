package sample.application;

import jdbc.RowMapper;
import sample.domain.PersonV3;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<PersonV3> {
    @Override
    public PersonV3 mapRow(ResultSet resultSet) throws SQLException {
        return new PersonV3(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getInt("age"),
                resultSet.getString("email"),
                null
        );

    }
}
