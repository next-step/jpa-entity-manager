package persistence.entity;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import persistence.entity.context.EntityEntry;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.StatefulPersistenceContext;
import persistence.entity.context.Status;
import persistence.entity.context.cache.EntitySnapshot;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.model.MappingMetaModel;

public class SimpleEntityManager implements EntityManager {

    private final MappingMetaModel mappingMetaModel;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(final MappingMetaModel mappingMetaModel, final EntityLoader entityLoader) {
        this.mappingMetaModel = mappingMetaModel;
        this.entityLoader = entityLoader;
        this.persistenceContext = createPersistenceContext();
    }

    protected PersistenceContext createPersistenceContext() {
        return new StatefulPersistenceContext();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object key) {
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(key, clazz);

        if (entityEntry == null) {
            final T found = entityLoader.load(clazz, key);
            persistenceContext.addEntity(key, found);

            return found;
        }

        if (entityEntry.is(Status.GONE)) {
            throw new EntityNotFoundException();
        } else if (entityEntry.is(Status.MANAGED)) {
            return persistenceContext.getEntity(key, clazz.getName());
        }

        entityEntry.setStatus(Status.LOADING);

        final T found = entityLoader.load(clazz, key);
        persistenceContext.addEntity(key, found);

        return found;
    }

    @Override
    public <T> T persist(final T entity) {
        final EntityPersister persister = mappingMetaModel.getEntityDescriptor(entity);

        final Object identifier = persister.getIdentifier(entity);

        final EntityEntry entityEntry = persistenceContext.getEntityEntry(identifier, entity);

        if (isExist(entityEntry)) {
            throw new EntityExistsException();
        }

        final Object key = persister.insert(entity);
        persister.setIdentifier(entity, key);

        persistenceContext.addEntity(key, entity);

        return entity;
    }

    @Override
    public <T> T merge(final T entity) {
        final EntityPersister persister = mappingMetaModel.getEntityDescriptor(entity);

        final Object identifier = persister.getIdentifier(entity);

        final EntityEntry entityEntry = persistenceContext.getEntityEntry(identifier, entity);

        if (!isExist(entityEntry)) {
            persister.update(entity);
            persistenceContext.addEntity(identifier, entity);

            return entity;
        } else if (entityEntry.is(Status.READ_ONLY)) {
            return entity;
        }

        final EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(identifier, entity);

        if (snapshot.checkDirty(entity)) {
            entityEntry.setStatus(Status.SAVING);
            persister.update(entity);
            persistenceContext.addEntity(identifier, entity);
        }

        return entity;
    }

    private boolean isExist(final EntityEntry entityEntry) {
        return entityEntry != null && entityEntry.is(Status.MANAGED);
    }

    @Override
    public void remove(final Object entity) {
        final EntityPersister persister = mappingMetaModel.getEntityDescriptor(entity);

        final Object identifier = persister.getIdentifier(entity);
        final EntityEntry entityEntry = persistenceContext.getEntityEntry(identifier, entity);

        if (entityEntry == null) {
            throw new EntityNotFoundException();
        } else if (entityEntry.is(Status.READ_ONLY)) {
            return;
        }

        entityEntry.setStatus(Status.DELETED);
        persistenceContext.removeEntity(identifier, entity);

        persister.delete(entity);
        entityEntry.setStatus(Status.GONE);
    }

}
