package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.dml.WhereClauseBuilder;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.EntityValue;
import persistence.sql.metadata.EntityValues;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	private final EntityMetadata entityMetadata;

	private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

	private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

	private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

	public EntityPersister(JdbcTemplate jdbcTemplate, EntityMetadata entityMetadata) {
		this.jdbcTemplate = jdbcTemplate;
		this.entityMetadata = entityMetadata;
	}

	public void insert(Object entity) {
		String query = insertQueryBuilder.buildQuery(entityMetadata, convertEntityToValues(entity));
		jdbcTemplate.execute(query);
	}

	public void delete(Object entity) {
		String query = deleteQueryBuilder.buildQuery(entityMetadata, new WhereClauseBuilder(entity));
        jdbcTemplate.execute(query);
	}

	public void update(Object entity) {
		String query = updateQueryBuilder.buildByIdQuery(entityMetadata, convertEntityToValues(entity), new WhereClauseBuilder(entity));
		jdbcTemplate.execute(query);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	private EntityValues convertEntityToValues(Object entity) {
		if(entity == null) {
			return null;
		}

		Field[] fields = entity.getClass().getDeclaredFields();

		return new EntityValues(Arrays.stream(fields)
				.map(x -> new EntityValue(x, entity))
				.filter(EntityValue::checkPossibleToBeValue)
				.collect(Collectors.toList()));
	}
}
