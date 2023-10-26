package hibernate.entity;

import hibernate.entity.column.EntityColumn;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
            final EntityPersister entityPersister,
            final EntityLoader entityLoader,
            final PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        Object persistenceContextEntity = persistenceContext.getEntity(new EntityKey(id, clazz));
        if (persistenceContextEntity != null) {
            return (T) persistenceContextEntity;
        }

        EntityClass<T> entityClass = EntityClass.getInstance(clazz);
        T loadEntity = entityLoader.find(entityClass, id);
        persistenceContext.addEntity(id, loadEntity);
        persistenceContext.getDatabaseSnapshot(id, loadEntity);
        return loadEntity;
    }

    @Override
    public void persist(final Object entity) {
        EntityColumn entityId = EntityClass.getInstance(entity.getClass())
                .getEntityId();
        validateAlreadyPersist(entity, entityId);

        Object generatedId = entityPersister.insert(entity);
        entityId.assignFieldValue(entity, generatedId);
        persistenceContext.addEntity(generatedId, entity);
        persistenceContext.getDatabaseSnapshot(generatedId, entity);
    }

    private void validateAlreadyPersist(Object entity, EntityColumn entityId) {
        Object id = entityId.getFieldValue(entity);
        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
    }

    @Override
    public void merge(Object entity) {

    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }
}
