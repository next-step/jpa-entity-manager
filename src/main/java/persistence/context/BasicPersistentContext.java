package persistence.context;

import java.util.HashMap;
import java.util.Map;

import static util.Utils.copyObject;

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

    @Override
    public Object getCachedDatabaseSnapshot(Long id) {
        Object snapshot = entitySnapShotByKey.get(id);
        if (snapshot == null) {
            throw new IllegalArgumentException(String.format("존재하지 않는 스냅 샷 id 입니다: %d", id));
        }
        return snapshot;
    }
}
