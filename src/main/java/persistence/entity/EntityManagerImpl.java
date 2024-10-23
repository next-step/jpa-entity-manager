package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.model.EntityPrimaryKey;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;

    // XXX: loader가 생기면 DmlQueryBuilder와 JdbcTemplate도 제거?
    private final DmlQueryBuilder queryBuilder;

    private final JdbcTemplate jdbcTemplate;

    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
            EntityPersister entityPersister,
            DmlQueryBuilder queryBuilder,
            JdbcTemplate jdbcTemplate,
            PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T findById(Class<T> clazz, Object id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity != null) {
            return entity;
        }

        String selectQuery = queryBuilder.buildSelectByIdQuery(clazz, id);
        return jdbcTemplate.queryForObject(selectQuery, resultSet ->
                new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void merge(Object entity) {
        Class<?> entityClass = entity.getClass();
        Object entityId = EntityPrimaryKey.build(entity).keyValue();
        Object existingEntity = persistenceContext.getEntity(entityClass, entityId);

        if (existingEntity != null) {
            entityPersister.update(entity);
        } else {
            persist(entity);
        }
    }
}
