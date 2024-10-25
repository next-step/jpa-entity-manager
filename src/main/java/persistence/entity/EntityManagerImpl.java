package persistence.entity;

import jdbc.JdbcTemplate;

import java.io.Serializable;
import java.util.function.Supplier;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate,
                             PersistenceContext persistenceContext) {

        this.persistenceContext = persistenceContext;
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        final EntityEntry entityEntry = getEntityEntryOrDefault(entityKey, () -> EntityEntry.loading((Serializable) id));

        if (entityEntry.isManaged()) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        if (entityEntry.isNotReadable()) {
            throw new IllegalArgumentException("Entity is not managed: " + clazz.getSimpleName());
        }

        final T loaded = entityLoader.loadEntity(clazz, entityKey);

        persistenceContext.addEntity(entityKey, loaded);
        persistenceContext.addDatabaseSnapshot(entityKey, loaded);
        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntry(entityKey, entityEntry);

        return loaded;
    }

    private EntityEntry getEntityEntryOrDefault(EntityKey entityKey, Supplier<EntityEntry> defaultEntrySupplier) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        if (entityEntry == null) {
            return defaultEntrySupplier.get();
        }

        return entityEntry;
    }

    @Override
    public void persist(Object entity) {
        if(entityPersister.hasId(entity)) {
            final EntityEntry entityEntry = persistenceContext.getEntityEntry(
                    new EntityKey(entityPersister.getEntityId(entity), entity.getClass())
            );

            if (entityEntry == null) {
                throw new IllegalArgumentException("No Entity Entry with id: " + entityPersister.getEntityId(entity));
            }

            if (entityEntry.isManaged()) {
                return;
            }

            throw new IllegalArgumentException("Entity already persisted");
        }

        final EntityEntry entityEntry = EntityEntry.inSaving();

        entityPersister.insert(entity);
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntry(entityKey, entityEntry);
    }

    @Override
    public void remove(Object entity) {
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);

        entityEntry.updateStatus(Status.DELETED);
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public <T> T merge(T entity) {
        final EntityKey entityKey = new EntityKey(entityPersister.getEntityId(entity), entity.getClass());
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        checkManagedEntity(entity, entityEntry);


        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (entitySnapshot.hasDirtyColumns(entity)) {
            entityPersister.update(entity);
        }

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
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
