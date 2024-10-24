package persistence.entity.impl;

import jdbc.JdbcTemplate;
import persistence.entity.EntityKey;
import persistence.entity.EntityPersister;
import persistence.entity.LongTypeId;
import persistence.entity.PendingEntities;
import persistence.entity.PersistedEntities;
import persistence.entity.PersistenceContext;

public class PersistenceContextImpl implements PersistenceContext {

    private final JdbcTemplate jdbcTemplate;
    private final PersistedEntities persistedEntities;
    private final PendingEntities pendingEntities;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        persistedEntities = new PersistedEntities();
        pendingEntities = new PendingEntities();
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        EntityKey entityKey = new EntityKey((Long) primaryKey, entityClass.getName());
        T entity = (T) persistedEntities.findEntity(entityKey);

        if (entity != null) {
            return entity;
        }

        entity = new EntityPersister<>(entityClass, jdbcTemplate).findById(primaryKey);
        persist(entity);

        return entity;
    }

    @Override
    public void persist(Object entity)  {
        if (new LongTypeId(entity).isEntityIdNull()) {
            pendingEntities.persistEntity(entity);
            return;
        }
        persistedEntities.persistEntity(getEntityKey(entity), entity);
    }

    @Override
    public void remove(Object entity) {
        pendingEntities.removeEntity(entity);
        persistedEntities.removeEntity(getEntityKey(entity));
    }

    @Override
    public void update(Object entity) throws IllegalAccessException {
       persistedEntities.persistEntity(getEntityKey(entity), entity);
    }

    @Override
    public void flush() throws IllegalAccessException {
        for (Object entity : pendingEntities.getEntities()) {
            new EntityPersister<>(entity.getClass(), jdbcTemplate).insert(entity);
        }
        for (Object entity : persistedEntities.getEntities()) {
            new EntityPersister<>(entity.getClass(), jdbcTemplate).update(entity);
        }
    }

    private EntityKey getEntityKey(Object entity) {
        return new EntityKey(
            new LongTypeId(entity).getId(),
            entity.getClass().getName()
        );
    }

}
