package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.query.SelectByIdQueryBuilder;

import java.io.Serializable;

public class EntityManagerImpl implements EntityManager {

    private static final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContextImpl persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Serializable) id, clazz);
        final Object managedEntity = persistenceContext.getEntity(entityKey);

        return managedEntity != null ? clazz.cast(managedEntity) : queryForObject(clazz, entityKey);
    }

    private <T> T queryForObject(Class<T> clazz, EntityKey id) {
        final String query = selectByIdQueryBuilder.build(clazz, id.getId());
        T queried = jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));

        persistenceContext.addEntity(id, queried);
        return queried;
    }

    @Override
    public void persist(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        if (persistenceContext.getEntity(entityKey) != null) {
            return;
        }

        entityPersister.insert(entity);
        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public void update(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.update(entity);
        persistenceContext.addEntity(entityKey, entity);
    }
}
