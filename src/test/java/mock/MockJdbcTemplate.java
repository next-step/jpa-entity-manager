package mock;

import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class MockJdbcTemplate extends JdbcTemplate {

    public MockJdbcTemplate() throws SQLException {
        super(new MockDatabaseServer().getConnection());
    }
}
