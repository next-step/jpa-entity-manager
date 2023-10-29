package persistence.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import persistence.meta.EntityMeta;

public class SimplePersistenceContext implements PersistenceContext {
    private final Map<Object, Object> context = new ConcurrentHashMap<>();
    private final Map<Object, Object> snapShotContext = new ConcurrentHashMap<>();

    @Override
    public Object getEntity(Object id) {
        return context.get(id);
    }

    @Override
    public void addEntity(Object id, Object entity) {
        context.put(id, entity);
    }

    @Override
    public void removeEntity(Object id) {
        context.remove(id);
        snapShotContext.remove(id);
    }

    @Override
    public Object getDatabaseSnapshot(Object id, Object entity) {
        EntityMeta entityMeta = new EntityMeta(entity.getClass());
        Object snapShot = entityMeta.createCopyEntity(entity);
        return snapShotContext.put(id, snapShot);
    }
    @Override
    public Object getCachedDatabaseSnapshot(Object id) {
        return snapShotContext.get(id);
    }
    @Override
    public List<Object> getChangedEntity() {
        return context.entrySet().stream()
                .filter(it -> !it.getValue().equals(snapShotContext.get(it.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        context.clear();
        snapShotContext.clear();
    }
}
