package persistence.entity;

import persistence.core.PersistenceEnvironment;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersisterProvider entityPersisterProvider;
    private final EntityLoaderProvider entityLoaderProvider;

    public SimpleEntityManager(final PersistenceEnvironment persistenceEnvironment) {
        this.entityPersisterProvider = persistenceEnvironment.getEntityPersisterProvider();
        this.entityLoaderProvider = persistenceEnvironment.getEntityLoaderProvider();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        final EntityLoader<T> entityLoader = entityLoaderProvider.getEntityLoader(clazz);
        return entityLoader.loadById(id);
    }

    @Override
    public void persist(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }

}
