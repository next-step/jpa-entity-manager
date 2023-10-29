package persistence.entity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import persistence.meta.EntityMeta;

public class SimplePersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> context = new ConcurrentHashMap<>();
    private final Map<EntityKey, Object> snapShotContext = new ConcurrentHashMap<>();

    @Override
    public Object getEntity(EntityKey entityKey) {
        return context.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        if (context.get(entityKey) == null) {
            this.getDatabaseSnapshot(entityKey, entity);
        }
        context.put(entityKey, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = EntityKey.of(entity);
        context.remove(key);
        snapShotContext.remove(key);
    }

    @Override
    public Object getDatabaseSnapshot(EntityKey entityKey, Object entity) {
        EntityMeta entityMeta = new EntityMeta(entity.getClass());
        Object snapShot = entityMeta.createCopyEntity(entity);
        return snapShotContext.put(entityKey, snapShot);
    }

    public List<Object> getChangedEntity() {
        return context.entrySet()
                .stream()
                .filter(it -> !it.getValue().equals(snapShotContext.get(it.getKey())))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    public void clear() {
        context.clear();
        snapShotContext.clear();
    }
}
