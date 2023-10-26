package hibernate.entity;

import hibernate.entity.column.EntityColumn;

import java.util.Map;

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
        persistNewEntity(loadEntity, id);
        return loadEntity;
    }

    private void persistNewEntity(Object entity, Object entityId) {
        persistenceContext.addEntity(entityId, entity);
        persistenceContext.getDatabaseSnapshot(entityId, entity);
    }

    @Override
    public void persist(final Object entity) {
        EntityColumn entityId = EntityClass.getInstance(entity.getClass())
                .getEntityId();
        validateAlreadyPersist(entity, entityId);

        Object generatedId = entityPersister.insert(entity);
        entityId.assignFieldValue(entity, generatedId);
        persistNewEntity(entity, generatedId);
    }

    private void validateAlreadyPersist(Object entity, EntityColumn entityId) {
        Object id = entityId.getFieldValue(entity);
        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
    }

    @Override
    public void merge(Object entity) {
        EntityClass<?> entityClass = EntityClass.getInstance(entity.getClass());
        Object entityId = entityClass.getEntityId()
                .getFieldValue(entity);
        EntitySnapshot snapshot = getSnapshot(entity, entityId);
        Map<EntityColumn, Object> changedColumns = snapshot.changedColumns(entity);
        if (changedColumns.isEmpty()) {
            return;
        }
        entityPersister.update(entityClass, entityId, changedColumns);
        persistNewEntity(entity, entityId);
    }

    private EntitySnapshot getSnapshot(Object entity, Object entityId) {
        EntityKey entityKey = new EntityKey(entityId, entity.getClass());
        EntitySnapshot snapshot = persistenceContext.getCachedDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            find(entity.getClass(), entityId);
            return persistenceContext.getCachedDatabaseSnapshot(entityKey);
        }
        return snapshot;
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }
}
