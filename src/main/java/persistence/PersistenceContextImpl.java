package persistence;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey<?>, Object> entityMap = new HashMap<>();
    private final Map<EntityKey<?>, Object> snapShotMap = new HashMap<>();

    @Override
    public Object findEntity(EntityKey<?> entityKey) {
        return entityMap.get(entityKey);
    }

    @Override
    public void insertEntity(EntityKey<?> entityKey, Object object) {
        this.entityMap.put(entityKey, object);
    }

    @Override
    public void deleteEntity(EntityKey<?> entityKey) {
        this.entityMap.remove(entityKey);
    }

    @Override
    public void addDatabaseSnapshot(EntityKey<?> entityKey, Object object) {
        this.snapShotMap.put(entityKey, object);
    }

    @Override
    public Object getDatabaseSnapshot(EntityKey<?> entityKey) {
        return this.snapShotMap.get(entityKey);
    }

}
