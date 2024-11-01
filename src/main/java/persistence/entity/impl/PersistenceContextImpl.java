package persistence.entity.impl;

import java.util.Collection;
import java.util.Set;
import jdbc.JdbcTemplate;
import persistence.entity.DatabaseSnapshots;
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
    private final DatabaseSnapshots databaseSnapshots;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        persistedEntities = new PersistedEntities();
        pendingEntities = new PendingEntities();
        databaseSnapshots = new DatabaseSnapshots();
    }

    @Override
    public <T> T getEntity(Class<T> entityClass, Object primaryKey) {
        EntityKey entityKey = new EntityKey((Long) primaryKey, entityClass.getName());
        T entity = entityClass.cast(persistedEntities.findEntity(entityKey));

        if (entity != null) {
            return entity;
        }

        entity = new EntityPersister<>(entityClass, jdbcTemplate).findById(primaryKey);
        addEntity(entity);

        return entity;
    }

    @Override
    public void addEntity(Object entity)  {
        if (new LongTypeId(entity).isEntityIdNull()) {
            pendingEntities.persistEntity(entity);
            return;
        }
        persistedEntities.persistEntity(getEntityKey(entity), entity);
        databaseSnapshots.addDatabaseSnapshot(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        pendingEntities.removeEntity(entity);
        persistedEntities.removeEntity(getEntityKey(entity));
    }

    @Override
    public void update(Object entity) throws IllegalAccessException {
       persistedEntities.persistEntity(getEntityKey(entity), entity);
    }

    @Override
    public Set<Object> getPendingEntities() {
        return pendingEntities.getEntities();
    }

    @Override
    public Collection<Object> getPersistedEntities() {
        return persistedEntities.getEntities();
    }

    @Override
    public Object getDatabaseSnapshot(Object entity) {
        return databaseSnapshots.getDatabaseSnapshot(entity);
    }

    private EntityKey getEntityKey(Object entity) {
        return new EntityKey(
            new LongTypeId(entity).getId(),
            entity.getClass().getName()
        );
    }

}
