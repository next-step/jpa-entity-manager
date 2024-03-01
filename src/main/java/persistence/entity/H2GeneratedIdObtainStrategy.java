package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;

public class H2GeneratedIdObtainStrategy implements GeneratedIdObtainStrategy {
    @Override
    public Long getGeneratedId(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForObject("CALL IDENTITY()", getNextId());
    }

    private RowMapper<Long> getNextId() {
        return rs -> rs.getLong(1);
    }

}
