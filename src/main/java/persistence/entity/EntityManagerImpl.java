package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final Map<Class<?>, EntityEntry> entityEntries;

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
        final EntityEntry entityEntry = createEntry(entityKey);

        if (entityEntry.isManaged()) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        final T loaded = entityLoader.loadEntity(clazz, entityKey);

        entityEntry.addEntity(entityKey, loaded);
        entityEntry.addDatabaseSnapshot(entityKey, loaded);
        entityEntry.updateStatus(Status.MANAGED);
        putEntry(loaded, entityEntry);

        return loaded;
    }

    private EntityEntry createEntry(EntityKey entityKey) {
        final Object entity = persistenceContext.getEntity(entityKey);
        if (entity == null) {
            return EntityEntry.loading(entityKey.getId(), persistenceContext);
        }

        return EntityEntry.managed(entityKey.getId(), persistenceContext);
    }

    @Override
    public void persist(Object entity) {
        final EntityEntry entityEntry = createEntry(entity);
        if (entityEntry.isManaged()) {
            return;
        }

        if (entityEntry.isNotSaving()) {
            throw new IllegalArgumentException("Entity already persisted");
        }

        final Object saved = entityPersister.insert(entity);
        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(saved),
                saved.getClass()
        );

        entityEntry.addEntity(entityKey, saved);
        entityEntry.addDatabaseSnapshot(entityKey, saved);
        entityEntry.updateStatus(Status.MANAGED);
        putEntry(entity, entityEntry);
    }

    private EntityEntry createEntry(Object entity) {
        if (!entityPersister.hasId(entity)) {
            return EntityEntry.inSaving(persistenceContext);
        }

        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final Object managedEntity = persistenceContext.getEntity(entityKey);

        if (managedEntity == null) {
            return EntityEntry.deleted(entityKey.getId(), persistenceContext);
        }

        return EntityEntry.managed(entityKey.getId(), persistenceContext);
    }

    private void putEntry(Object entity, EntityEntry entityEntry) {
        entityEntries.put(entity.getClass(), entityEntry);
    }

    @Override
    public void remove(Object entity) {
        final EntityEntry entityEntry = entityEntries.get(entity.getClass());
        checkManagedEntity(entity, entityEntry);

        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.delete(entity);
        entityEntry.removeEntity(entityKey);
        entityEntry.updateStatus(Status.DELETED);
    }

    @Override
    public <T> T merge(T entity) {
        final EntityEntry entityEntry = entityEntries.get(entity.getClass());
        checkManagedEntity(entity, entityEntry);

        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);
        final Object managedEntity = persistenceContext.getEntity(entityKey);

        if (entityEntry.hasDirtyColumns(entitySnapshot, managedEntity)) {
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
