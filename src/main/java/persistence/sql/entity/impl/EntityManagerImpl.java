package persistence.sql.entity.impl;

import persistence.sql.entity.EntityLoader;
import persistence.sql.entity.EntityManager;
import persistence.sql.entity.EntityPersister;
import persistence.sql.entity.PersistenceContext;
import persistence.sql.entity.exception.MergeFailureException;
import persistence.sql.entity.exception.PersistFailureException;

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

        if (Objects.isNull(persistenceContext.getEntity(key))) {
            final T instance = entityLoader.findById(clazz, id);
            persistenceContext.addEntity(key, instance);
            return instance;
        }

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public Object persist(final Object entity) {
        final EntityKey entityKey = EntityKey.fromEntity(entity);
        final EntityEntry existEntityEntry = persistenceContext.getEntityEntry(entityKey);

        if (existEntityEntry != null && existEntityEntry.isReadOnly()) {
            throw new PersistFailureException();
        }

        final EntityEntry entityEntry = EntityEntry.of(Status.SAVING);
        persistenceContext.addEntityEntry(entityKey, entityEntry);

        final Long id = entityPersister.insert(entity);
        final EntityKey key = EntityKey.fromNameAndValue(entity.getClass().getName(), id);
        entityEntry.updateStatus(Status.MANAGED);
        persistenceContext.addEntity(key, entity, entityEntry);

        return entity;
    }

    @Override
    public Object merge(final Object entity) {
        final EntityKey key = EntityKey.fromEntity(entity);
        final EntityEntry existEntityEntry = persistenceContext.getEntityEntry(key);

        if (existEntityEntry != null && existEntityEntry.isReadOnly()) {
            throw new MergeFailureException();
        }

        final EntityEntry entityEntry = EntityEntry.of(Status.SAVING);
        persistenceContext.addEntityEntry(key, entityEntry);

        if (persistenceContext.isDirty(key, entity)) {
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

        persistenceContext.removeEntity(key);
        entityPersister.delete(entity);
    }

    public boolean isDirty(Object entity) {
        final EntityKey key = EntityKey.fromEntity(entity);

        return persistenceContext.isDirty(key, entity);
    }
}
