package persistence.entity;

import jakarta.persistence.Id;
import jdbc.EntityMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereClauseBuilder;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.EntityValue;
import persistence.sql.metadata.EntityValues;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityLoader {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

	public EntityLoader(JdbcTemplate jdbcTemplate, EntityMetadata entityMetadata) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = entityMetadata;
	}

	public <T> T find(Class<T> clazz, Long id) {
		EntityValues entityValues = new EntityValues(Arrays.stream(clazz.getDeclaredFields())
				.filter(x -> x.isAnnotationPresent(Id.class))
				.map(x -> new EntityValue(x, String.valueOf(id)))
				.collect(Collectors.toList()));

		String query = selectQueryBuilder.buildFindByIdQuery(entityMetadata, new WhereClauseBuilder(entityValues));
		return jdbcTemplate.queryForObject(query, new EntityMapper<>(clazz));
	}
}
