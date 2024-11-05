package persistence.entity;

import persistence.model.EntityPrimaryKey;

import java.util.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> entityCache = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> entityClass, Object id) {
        EntityKey cacheKey = createEntityKey(entityClass, id);
        return entityClass.cast(entityCache.get(cacheKey));
    }

    @Override
    public void addEntity(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);
        EntitySnapshot snapshot = new EntitySnapshot(entityObject);

        entityCache.put(entityKey, entityObject);
        entitySnapshots.put(entityKey, snapshot);
    }

    @Override
    public void removeEntity(Object entityObject) {
        EntityKey cacheKey = createEntityKey(entityObject);
        entityCache.remove(cacheKey);
        entitySnapshots.remove(cacheKey);
    }

    @Override
    public void updateEntity(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);

        if (!entityCache.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY NOT EXISTS");
        }

        EntitySnapshot snapshot = entitySnapshots.get(entityKey);
        if (snapshot.shouldBeDirty(entityObject)) {
            entityCache.put(entityKey, entityObject);
            snapshot.update(entityObject);
        }
    }

    @Override
    public EntitySnapshot getSnapshot(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);
        return entitySnapshots.get(entityKey);
    }

    @Override
    public List<EntitySnapshot> getDirtySnapshots() {
        return entitySnapshots.values().stream()
                .filter(EntitySnapshot::isDirty)
                .toList();
    }

    @Override
    public boolean isEntityExists(Object entityObject) {
        Class<?> entityClass = entityObject.getClass();
        Object entityId = EntityPrimaryKey.build(entityObject).keyValue();
        final EntityKey entityKey = createEntityKey(entityClass, entityId);
        return entityCache.containsKey(entityKey);
    }

    private EntityKey createEntityKey(Class<?> entityClass, Object id) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityClass, id);
        return new EntityKey(entityClass, primaryKey);
    }

    private EntityKey createEntityKey(Object entityObject) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityObject);
        return new EntityKey(entityObject.getClass(), primaryKey);
    }
}
