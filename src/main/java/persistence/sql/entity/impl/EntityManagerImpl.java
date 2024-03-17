package persistence.sql.entity.impl;

import jakarta.persistence.EntityExistsException;
import persistence.sql.entity.EntityLoader;
import persistence.sql.entity.EntityManager;
import persistence.sql.entity.EntityPersister;
import persistence.sql.entity.PersistenceContext;

import java.util.Objects;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        final EntityKey key = EntityKey.fromNameAndValue(clazz.getName(), id);
        final EntityEntry existEntityEntry = persistenceContext.getEntityEntry(key);

        if (existEntityEntry != null && existEntityEntry.isGone()) {
            throw new IllegalArgumentException();
        }

        if (Objects.isNull(persistenceContext.getEntity(key))) {
            final EntityEntry entityEntry = EntityEntry.of(Status.LOADING);
            final T instance = entityLoader.findById(clazz, id);

            entityEntry.updateStatus(Status.MANAGED);
            persistenceContext.addEntity(key, instance, entityEntry);
            return instance;
        }

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public Object persist(final Object entity) {
        checkDetachedEntity(entity);

        checkExistEntity(entity);

        final EntityEntry entityEntry = EntityEntry.of(Status.SAVING);

        final Long id = entityPersister.insert(entity);
        final EntityKey key = EntityKey.fromNameAndValue(entity.getClass().getName(), id);
        entityEntry.updateStatus(Status.MANAGED);

        final Object persistEntity = find(entity.getClass(), id);
        persistenceContext.addEntity(key, persistEntity, entityEntry);

        return persistEntity;
    }

    private void checkDetachedEntity(final Object entity) {
        if (EntityKey.isDetachedEntity(entity)) {
            throw new IllegalArgumentException();
        }
    }

    private void checkExistEntity(final Object entity) {
        if (!EntityKey.isDetachedEntity(entity)) {
            return;
        }

        final EntityKey entityKey = EntityKey.fromEntity(entity);

        if (persistenceContext.contains(entityKey)) {
            throw new EntityExistsException();
        }
    }

    @Override
    public Object merge(final Object entity) {
        final EntityKey key = EntityKey.fromEntity(entity);
        final EntityEntry existEntityEntry = persistenceContext.getEntityEntry(key);

        if (existEntityEntry != null && existEntityEntry.isGone()) {
            throw new IllegalArgumentException();
        }

        final EntityEntry entityEntry = EntityEntry.of(Status.SAVING);

        if (persistenceContext.isDirty(key, entity) && existEntityEntry.isNotReadOnly()) {
            entityPersister.update(entity);
        } else {
            entityPersister.insert(entity);
        }

        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntity(key, entity, entityEntry);
        return entityLoader.findById(entity.getClass(), (Long) key.value());
    }


    @Override
    public void remove(final Object entity) {
        final EntityKey key = EntityKey.fromEntity(entity);
        final EntityEntry existEntityEntry = persistenceContext.getEntityEntry(key);

        if (existEntityEntry != null && existEntityEntry.isReadOnly()) {
            throw new IllegalArgumentException();
        }

        final EntityEntry entityEntry = EntityEntry.of(Status.DELETED);

        entityEntry.updateStatus(Status.GONE);
        persistenceContext.removeEntity(key, entityEntry);
        entityPersister.delete(entity);
    }

    public boolean isDirty(Object entity) {
        final EntityKey key = EntityKey.fromEntity(entity);

        return persistenceContext.isDirty(key, entity);
    }
}
