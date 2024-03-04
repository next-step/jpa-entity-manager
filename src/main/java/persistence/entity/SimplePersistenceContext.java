package persistence.entity;

import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<Long, Object> cache;
    private final Map<Long, Object> snapshot;

    public SimplePersistenceContext() {
        this.cache = new HashMap<>();
        this.snapshot = new HashMap<>();
    }

    @Override
    public Object getEntity(Long id) {
        return cache.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        bindEntityId(entity, id);
        cache.put(id, entity);
        snapshot.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Long id = getEntityId(entity);
        cache.remove(id);
    }

    @Override
    public boolean isCached(Object entity) {
        Long entityId = getEntityId(entity);
        Object cachedEntity = getEntity(entityId);
        return cachedEntity != null;
    }

    private Long getEntityId(Object entity) {
        Table table = new Table(entity.getClass());

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        return (Long) entityBinder.getValue(pkColumn);
    }

    private void bindEntityId(Object entity, Long id) {
        Table table = new Table(entity.getClass());

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        entityBinder.bindValue(pkColumn, id);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        return snapshot.get(id);
    }
}
