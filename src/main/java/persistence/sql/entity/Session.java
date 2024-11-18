package persistence.sql.entity;

import jakarta.persistence.PersistenceException;
import java.util.Map;
import java.util.Optional;

public class Session implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public Session(final EntityPersister entityPersister,
                   final EntityLoader entityLoader,
                   final PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public void persist(final Object entity) {
        final CacheKey key = createKey(entity);

        if (persistenceContext.contains(key)) {
            return;
        }

        try {
            final Object primaryKey = entityLoader.getMaxId(entity.getClass());
            persistenceContext.prePersist(entity, primaryKey);
        } catch (final RuntimeException e) {
            persistenceContext.handlePersistError(key);
            throw new PersistenceException("Entity persist failed", e);
        }
    }

    @Override
    public void remove(final Object entity) {
        final CacheKey key = createKey(entity);

        if (!persistenceContext.containsEntity(entity.getClass(), extractId(entity))) {
            throw new IllegalArgumentException("Entity must be managed to be removed");
        }

        final Status currentStatus = persistenceContext.getEntityStatus(key);

        if (currentStatus == Status.DELETED || currentStatus == Status.GONE) {
            throw new IllegalStateException("Entity already deleted: %s".formatted(key.className()));
        }
        if (currentStatus == Status.SAVING || currentStatus == Status.LOADING) {
            throw new IllegalStateException("Cannot remove entity while %s: %s".formatted(currentStatus.name().toLowerCase(), key.className()));
        }
        if (currentStatus == Status.READ_ONLY) {
            throw new IllegalStateException("Cannot remove read-only entity: %s".formatted(key.className()));
        }

        persistenceContext.markAsDeleted(key);
    }

    public <T> T find(final Class<T> entityClass, final Long id) {
        final CacheKey key = new CacheKey(entityClass.getSimpleName(), id);

        final Optional<Object> cachedEntity = persistenceContext.getEntity(key);
        if (cachedEntity.isPresent()) {
            return entityClass.cast(cachedEntity.get());
        }

        persistenceContext.preLoad(entityClass, id);
        final T loadedEntity = entityLoader.select(entityClass, id);
        persistenceContext.postLoad(loadedEntity, id);
        return loadedEntity;
    }

    public void flush() {
        final Map<CacheKey, Object> dirtyEntities = persistenceContext.getDirtyEntities();

        for (final Map.Entry<CacheKey, Object> entry : dirtyEntities.entrySet()) {
            final CacheKey key = entry.getKey();
            final Object entity = entry.getValue();
            final Status status = persistenceContext.getEntityStatus(key);

            try {
                switch (status) {
                    case MANAGED:
                        entityPersister.update(entity);
                        break;
                    case SAVING:
                        final Object primaryKey = entityLoader.getMaxId(entity.getClass());
                        entityPersister.insert(entity);
                        persistenceContext.postPersist(entity, primaryKey);
                        break;
                    case DELETED:
                        entityPersister.delete(entity);
                        persistenceContext.removeEntity(key);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected entity status: " + status);
                }
            } catch (IllegalStateException e) {
                throw new RuntimeException(e);
            } catch (final Exception e) {
                throw new IllegalStateException("Failed to flush entity: " + key.className(), e);
            }
        }
    }
    public <T> T merge(final T entity) {
        final CacheKey key = createKey(entity);
        final Long id = extractId(entity);

        if (persistenceContext.containsEntity(entity.getClass(), id)) {
            persistenceContext.managedEntity(key, entity);
            return entity;
        }

        persistenceContext.prePersist(entity, id);
        return entity;
    }
    private CacheKey createKey(final Object entity) {
        return new CacheKey(
                entity.getClass().getSimpleName(),
                extractId(entity)
        );
    }

    private Long extractId(final Object entity) {
        return new EntityId(entity).extractId();
    }
}
