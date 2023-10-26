package hibernate.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;

    public SimplePersistenceContext(final Map<EntityKey, Object> entities) {
        this.entities = entities;
    }

    public SimplePersistenceContext() {
        this(new ConcurrentHashMap<>());
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
    public void removeEntity(Object entity) {
        EntityKey entityKey = entities.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(entity))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("영속화되어있지 않은 entity입니다."));
        entities.remove(entityKey);
    }
}
