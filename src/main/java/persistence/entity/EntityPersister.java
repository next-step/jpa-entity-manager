package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	public EntityPersister(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Object insert(Object entity) {
		String query = DmlQueryBuilder.build().insertQuery(new EntityMetadata(entity));
		return jdbcTemplate.executeUpdate(query);
	}

	public void delete(Object entity) {
		String query = DmlQueryBuilder.build().deleteQuery(new EntityMetadata(entity));
        jdbcTemplate.execute(query);
	}

	public void update(Field[] fields, Object entity) {
		String query = DmlQueryBuilder.build().updateQuery(
				new Columns(Arrays.stream(fields).map(x -> {
					try {
						return new Column(x, String.valueOf(x.get(entity)));
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList())),
				new EntityMetadata(entity)
		);
		jdbcTemplate.execute(query);
	}
}
