package hibernate.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, EntitySnapshot> snapshotEntities;

    public SimplePersistenceContext(final Map<EntityKey, Object> entities, final Map<EntityKey, EntitySnapshot> snapshotEntities) {
        this.entities = entities;
        this.snapshotEntities = snapshotEntities;
    }

    public SimplePersistenceContext() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    @Override
    public Object getEntity(final Object id) {
        return entities.get(id);
    }

    @Override
    public void addEntity(final Object id, final Object entity) {
        entities.put(new EntityKey(id, entity.getClass()), entity);
    }

    @Override
    public void removeEntity(final Object entity) {
        EntityKey entityKey = entities.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(entity))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("영속화되어있지 않은 entity입니다."));
        entities.remove(entityKey);
    }

    @Override
    public Object getDatabaseSnapshot(final Object id, final Object entity) {
        EntitySnapshot entitySnapshot = new EntitySnapshot(entity);
        return snapshotEntities.put(new EntityKey(id, entity.getClass()), entitySnapshot);
    }
}
