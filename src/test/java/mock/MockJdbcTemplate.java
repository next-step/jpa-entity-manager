package mock;

import jdbc.JdbcTemplate;

public class MockJdbcTemplate extends JdbcTemplate {

    public MockJdbcTemplate(){
        super(new MockDatabaseServer().getConnection());
    }
}
