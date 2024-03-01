package persistence.entity;

import jdbc.JdbcTemplate;

public interface GeneratedIdObtainStrategy {
    Long getGeneratedId(JdbcTemplate jdbcTemplate);
}
