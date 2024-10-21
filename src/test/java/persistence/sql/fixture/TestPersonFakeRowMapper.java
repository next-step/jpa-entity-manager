package persistence.sql.fixture;

import jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestPersonFakeRowMapper implements RowMapper<TestPerson> {
    @Override
    public TestPerson mapRow(ResultSet resultSet) throws SQLException {
        return new TestPerson(
                resultSet.getLong("id"),
                resultSet.getString("nick_name"),
                resultSet.getInt("old"),
                resultSet.getString("email"),
                null
        );
    }
}
