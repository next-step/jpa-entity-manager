package jpa;

import persistence.sql.exception.CouldNotAccessField;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.model.DatabaseSnapshot;
import persistence.sql.model.EntityId;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

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

    public boolean isDirty(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        Object managedEntity = entityMap.get(entityInfo);
        Object snapshotEntity = snapshotMap.get(entityInfo);

        if (managedEntity == null || snapshotEntity == null) {
            return false;
        }

        if (isNotEqualFields(managedEntity, snapshotEntity)) {
            return true;
        }

        return false;
    }

    private EntityInfo<?> makeEntityInfo(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        Long idValue = entityId.getIdValue(entity);
        return new EntityInfo<>(entity.getClass(), idValue);
    }

    private boolean isNotEqualFields(Object managedEntity, Object snapshotEntity) {
        for (Field field : managedEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object managedValue = field.get(managedEntity);
                Object snapshotValue = field.get(snapshotEntity);
                if (!managedValue.equals(snapshotValue)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                throw new CouldNotAccessField(e, ExceptionMessage.COULD_NOT_ACCESS_FIELD);
            }
        }
        return false;
    }
}
