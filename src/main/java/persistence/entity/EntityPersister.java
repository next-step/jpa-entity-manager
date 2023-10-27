package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

	private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

	private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

	public EntityPersister(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insert(Object entity) {
		String query = insertQueryBuilder.buildQuery(new EntityMetadata(entity));
		jdbcTemplate.execute(query);
	}

	public void delete(Object entity) {
		String query = deleteQueryBuilder.buildByIdQuery(new EntityMetadata(entity));
        jdbcTemplate.execute(query);
	}

	public void update(Object entity) {
		String query = updateQueryBuilder.buildByIdQuery(new EntityMetadata(entity));
		jdbcTemplate.execute(query);
	}
}
