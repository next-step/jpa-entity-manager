package persistence.entity;

import jdbc.EntityMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityLoader {
	private final JdbcTemplate jdbcTemplate;

	public EntityLoader(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public <T> T find(Class<T> clazz, Long id) {
		String query = DmlQueryBuilder.build().selectQuery(clazz, String.valueOf(id));
		return jdbcTemplate.queryForObject(query, new EntityMapper<>(clazz));
	}
}
