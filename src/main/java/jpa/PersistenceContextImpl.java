package jpa;

import persistence.sql.exception.CouldNotAccessField;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.model.DatabaseSnapshot;
import persistence.sql.model.EntityId;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityInfo<?>, Object> entityMap = new HashMap<>();
    private final Map<EntityInfo<?>, DatabaseSnapshot> snapshotMap = new HashMap<>();

    public PersistenceContextImpl() {
    }

    @Override
    public void add(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        if (entityMap.containsKey(entityInfo)) {
            return;
        }
        entityMap.put(entityInfo, entity);
    }

    @Override
    public <T> T get(Class<T> clazz, Long id) {
        EntityInfo<?> entityInfo = new EntityInfo<>(clazz, id);
        Object entity = entityMap.get(entityInfo);
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
        snapshotMap.put(entityInfo, new DatabaseSnapshot(snapshotEntity));
    }

    @Override
    public void removeDatabaseSnapshot(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        snapshotMap.remove(entityInfo);
    }

    @Override
    public boolean isDirty(Object entity) {
        EntityInfo<?> entityInfo = makeEntityInfo(entity);
        DatabaseSnapshot databaseSnapshot = snapshotMap.get(entityInfo);
        return databaseSnapshot != null && databaseSnapshot.isDirty(entity);
    }


    @Override
    public List<Object> getDirtyEntities() {
        return snapshotMap.keySet().stream()
                .filter(key -> snapshotMap.get(key).isDirty(entityMap.get(key)))
                .map(entityMap::get)
                .collect(Collectors.toList());
    }

    private EntityInfo<?> makeEntityInfo(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        Long idValue = entityId.getIdValue(entity);
        return new EntityInfo<>(entity.getClass(), idValue);
    }


}
