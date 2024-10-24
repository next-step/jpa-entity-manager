package persistence.entity;

import jdbc.JdbcTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final Map<EntityKey, EntityEntry> entityEntries;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate,
                             PersistenceContext persistenceContext) {

        this.persistenceContext = persistenceContext;
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityEntries = new HashMap<>();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        final EntityEntry entityEntry = getEntityEntry(entityKey);

        if (entityEntry.isManaged()) {
            return clazz.cast(entityEntry.getEntity(entityKey));
        }

        final T loaded = entityLoader.loadEntity(clazz, entityKey);

        entityEntry.addEntity(entityKey, loaded);
        entityEntry.addDatabaseSnapshot(entityKey, loaded);
        entityEntry.updateStatus(Status.MANAGED);
        putEntry(entityKey, entityEntry);

        return loaded;
    }

    private EntityEntry getEntityEntry(EntityKey entityKey) {
        final Object entity = persistenceContext.getEntity(entityKey);
        if (entity == null) {
            return EntityEntry.loading(entityKey.getId(), persistenceContext);
        }

        return EntityEntry.managed(entityKey.getId(), persistenceContext);
    }

    @Override
    public void persist(Object entity) {
        final EntityEntry entityEntry = getEntityEntry(entity);
        if (entityEntry.isManaged()) {
            return;
        }

        if (entityEntry.isNotSaving()) {
            throw new IllegalArgumentException("Entity already persisted");
        }

        entityPersister.insert(entity);
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());

        entityEntry.addEntity(entityKey, entity);
        entityEntry.addDatabaseSnapshot(entityKey, entity);
        entityEntry.updateStatus(Status.MANAGED);
        putEntry(entityKey, entityEntry);
    }

    private EntityEntry getEntityEntry(Object entity) {
        if (!entityPersister.hasId(entity)) {
            return EntityEntry.inSaving(persistenceContext);
        }

        final Serializable id = entityPersister.getEntityId(entity);
        final EntityEntry entityEntry = entityEntries.get(new EntityKey(id, entity.getClass()));

        if (entityEntry == null) {
            return EntityEntry.deleted(id, persistenceContext);
        }

        return entityEntry;
    }

    private void putEntry(EntityKey entityKey, EntityEntry entityEntry) {
        entityEntries.put(entityKey, entityEntry);
    }

    @Override
    public void remove(Object entity) {
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = entityEntries.get(entityKey);
        checkManagedEntity(entity, entityEntry);


        entityPersister.delete(entity);
        entityEntry.removeEntity(entityKey);
        entityEntry.updateStatus(Status.DELETED);
    }

    @Override
    public <T> T merge(T entity) {
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = entityEntries.get(entityKey);
        checkManagedEntity(entity, entityEntry);


        if (entityEntry.hasDirtyColumns(entityKey)) {
            entityPersister.update(entity);
        }

        entityEntry.addEntity(entityKey, entity);
        entityEntry.addDatabaseSnapshot(entityKey, entity);
        return entity;
    }

    private void checkManagedEntity(Object entity, EntityEntry entityEntry) {
        if (entityEntry == null) {
            throw new IllegalStateException("Can not find entity in persistence context: "
                    + entity.getClass().getSimpleName());
        }

        if (!entityEntry.isManaged()) {
            throw new IllegalArgumentException("Detached entity can not be merged: "
                    + entity.getClass().getSimpleName());
        }
    }
}
