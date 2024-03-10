package persistence.entity;

import persistence.entity.context.PersistenceContext;
import persistence.entity.context.StatefulPersistenceContext;
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
        final T cachedEntity = persistenceContext.getEntity(key, clazz.getName());
        if (cachedEntity != null) {
            return cachedEntity;
        }

        return entityLoader.load(clazz, key);
    }

    @Override
    public void persist(final Object entity) {
        final String entityName = entity.getClass().getName();
        final EntityPersister persister = mappingMetaModel.getEntityDescriptor(entityName);

        final Object key = persister.insert(entity);

        persistenceContext.addEntity(key, entity);
    }

    @Override
    public void remove(final Object entity) {
        final String entityName = entity.getClass().getName();
        final EntityPersister persister = mappingMetaModel.getEntityDescriptor(entityName);

        final Object identifier = persister.getIdentifier(entity);
        persistenceContext.removeEntity(identifier, entity);

        persister.delete(entity);
    }

}
