package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.query.SelectByIdQueryBuilder;

public class EntityLoader {
    private static final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final String query = selectByIdQueryBuilder.build(entityClass, entityKey.getId());

        final Object queried = jdbcTemplate.queryForObject(query,
                new EntityRowMapper<>(entityKey.getEntityClass())
        );

        return entityClass.cast(queried);
    }
}
