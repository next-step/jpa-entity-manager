package persistence.entity;

import jdbc.EntityMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

public class EntityLoader {
	private final JdbcTemplate jdbcTemplate;

	private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

	public EntityLoader(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public <T> T find(Class<T> clazz, Long id) {
		String query = selectQueryBuilder.buildFindByIdQuery(clazz, String.valueOf(id));
		return jdbcTemplate.queryForObject(query, new EntityMapper<>(clazz));
	}
}
