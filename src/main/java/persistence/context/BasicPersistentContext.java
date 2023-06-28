package persistence.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BasicPersistentContext implements PersistenceContext {
    private final Map<Long, Object> entityByKey = new HashMap<>();
    private final Map<Long, Object> entitySnapShotByKey = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entityByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entityByKey.put(id, entity);
    }

    @Override
    public void removeEntity(Long id) {
        entityByKey.remove(id);
        entitySnapShotByKey.remove(id);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        return entitySnapShotByKey.put(id, copyObject(entity));
    }

    public static <T> T copyObject(T source) {
        Class<?> clazz = source.getClass();
        T copy;
        copy = null;
        try {
            copy = (T) clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(copy, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return copy;
    }

    @Override
    public Object getCachedDatabaseSnapshot(Long id) {
        Object snapshot = entitySnapShotByKey.get(id);
        if (snapshot == null) {
            throw new IllegalArgumentException(String.format("존재하지 않는 스냅 샷 id 입니다: %d", id));
        }
        return snapshot;
    }
}
