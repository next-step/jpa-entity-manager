package jpa;

import org.jetbrains.annotations.NotNull;
import persistence.sql.model.DatabaseSnapshot;
import persistence.sql.model.EntityId;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityInfo<?>, Object> entityMap = new HashMap<>();
    private final Map<EntityInfo<?>, Object> snapshotMap = new HashMap<>();

    public PersistenceContextImpl() {
    }

    @Override
    public void add(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        snapshotMap.put(entityInfo, entity);
        if (entityMap.containsKey(entityInfo)) {
            return;
        }
        entityMap.put(entityInfo, entity);
    }

    @Override
    public <T> T get(Class<T> clazz, Long id) {
        EntityInfo<?> entityInfo = new EntityInfo<>(clazz, id);
        Object entity = entityMap.get(entityInfo);
        snapshotMap.put(entityInfo, entity);
        if (entity == null) {
            return null;
        }
        return clazz.cast(entity);
    }

    @Override
    public void remove(Object entity) {
        entityMap.remove(makeEntityInfo(entity));
    }

    @Override
    public void update(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        entityMap.put(entityInfo, entity);
    }

    @Override
    public <T> T getDatabaseSnapshot(T entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        return (T) snapshotMap.get(entityInfo);
    }

    @Override
    public void createDatabaseSnapshot(Object entity) {
        DatabaseSnapshot databaseSnapshot = new DatabaseSnapshot(entity);
        Object snapshotEntity = databaseSnapshot.getEntity();

        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        snapshotMap.put(entityInfo, snapshotEntity);
    }

    @Override
    public void removeDatabaseSnapshot(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        snapshotMap.remove(entityInfo);
    }

    public boolean isDirty() {
        for (EntityInfo<?> entityInfo : entityMap.keySet()) {
            Object entity = entityMap.get(entityInfo);
            Object snapshotEntity = snapshotMap.get(entityInfo);

            if (entity == null || snapshotEntity == null) {
                continue;
            }

            if (!Objects.equals(entity, snapshotEntity)) {
                return true;
            }
        }
        return false;
    }

    private EntityInfo<?> makeEntityInfo(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        Long idValue = entityId.getIdValue(entity);
        return new EntityInfo<>(entity.getClass(), idValue);
    }
}
