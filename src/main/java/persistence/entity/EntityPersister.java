package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;

public class EntityPersister {
	private final JdbcTemplate jdbcTemplate;

	public EntityPersister(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Object insert(EntityMetadata entityMetadata) {
		String query = DmlQueryBuilder.build().insertQuery(entityMetadata);
		return jdbcTemplate.executeUpdate(query);
	}

	public void delete(EntityMetadata entityMetadata) {
		String query = DmlQueryBuilder.build().deleteQuery(entityMetadata);
        jdbcTemplate.execute(query);
	}

	public void update(Columns columns, EntityMetadata entityMetadata) {
		String query = DmlQueryBuilder.build().updateQuery(columns, entityMetadata);
		jdbcTemplate.execute(query);
	}
}
