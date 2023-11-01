package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

public class SimpleEntityManager implements EntityManager{
    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistenceContext.getEntity(clazz, id);

        if(entity == null) {
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
        }

        return clazz.cast(entity);
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(new EntityMetadata(entity).getPKValue(), entity);
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
