package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.meta.EntityMeta;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<Object, Object> context = new ConcurrentHashMap<>();
    private final EntityMeta entityMeta;
    public DefaultPersistenceContext(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    @Override
    public Object getEntity(Object id) {
        return context.get(id);
    }

    @Override
    public void addEntity(Object id, Object entity) {
        context.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        context.remove(entityMeta.getPkValue(entity));
    }
}
