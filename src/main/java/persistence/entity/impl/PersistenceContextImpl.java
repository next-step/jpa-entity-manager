package persistence.entity.impl;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.PersistenceContext;
import persistence.sql.EntityId;
import persistence.sql.EntityMetadata;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityId, Object> entityIdToEntityMap = new HashMap<>();
    private final Map<EntityId, Object> entityIdToDatabaseSnapshotMap = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> entityClass, Object id) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        EntityId entityId = entityMetadata.getEntityIdFrom(entityClass, id);

        return entityClass.cast(
            entityIdToEntityMap.get(entityId)
        );
    }

    @Override
    public void addEntity(Object id, Object entity) {
        EntityId entityId = new EntityId(entity.getClass(), id);

        entityIdToEntityMap.put(entityId, entity);
    }

    @Override
    public <T> void removeEntity(T entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());

        EntityId entityId = entityMetadata.getEntityIdFrom(entity);

        entityIdToEntityMap.remove(entityId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getDatabaseSnapshot(Object id, T entity) {
        EntityId entityId = new EntityId(entity.getClass(), id);

        return (T) entityIdToDatabaseSnapshotMap.computeIfAbsent(entityId, key -> entity);
    }
}
