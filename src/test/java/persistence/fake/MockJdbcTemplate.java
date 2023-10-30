package persistence.fake;

import jdbc.JdbcTemplate;

public class MockJdbcTemplate extends JdbcTemplate {
    public MockJdbcTemplate() {
        super(null);
    }
}
