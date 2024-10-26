package persistence;

import java.io.*;
import java.lang.reflect.Field;
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
        this.entityMap.put(entityKey, deepCopy(object));
    }

    @Override
    public void deleteEntity(EntityKey<?> entityKey) {
        this.entityMap.remove(entityKey);
    }

    @Override
    public void insertDatabaseSnapshot(EntityKey<?> entityKey, Object object) {
        this.snapShotMap.put(entityKey, deepCopy(object));
    }

    @Override
    public Object getDatabaseSnapshot(EntityKey<?> entityKey) {
        return this.snapShotMap.get(entityKey);
    }

    private Object deepCopy(Object original) {
        if (original == null) return null;

        try {
            Class<?> clazz = original.getClass();
            Object copy = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);

                Object value = field.get(original);
                field.set(copy, value);
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }

}
