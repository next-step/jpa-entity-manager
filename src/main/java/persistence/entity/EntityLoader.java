package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.query.SelectByIdQueryBuilder;

public class EntityLoader {
    private static final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;

    public EntityLoader(JdbcTemplate jdbcTemplate,
                        PersistenceContext persistenceContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
    }

    public <T> T loadEntity(Class<T> entityClass, EntityKey entityKey) {
        final Object managedEntity = persistenceContext.getEntity(entityKey);
        if (managedEntity != null) {
            return entityClass.cast(managedEntity);
        }

        final T entity = queryEntity(entityClass, entityKey);
        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
        return entity;
    }

    private <T> T queryEntity(Class<T> entityClass, EntityKey entityKey) {
        final String query = selectByIdQueryBuilder.build(entityClass, entityKey.getId());

        final Object queried = jdbcTemplate.queryForObject(query,
                new EntityRowMapper<>(entityKey.getEntityClass())
        );

        return entityClass.cast(queried);
    }
}
