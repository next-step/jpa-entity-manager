package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.Objects;

public class DefaultEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    private DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(new DefaultPersistenceContext(), new DefaultEntityPersister(jdbcTemplate));
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (Objects.nonNull(managedEntity)) {
            return managedEntity;
        }

        final T entity = entityPersister.find(entityType, id);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void update(Object entity) {
        entityPersister.update(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
