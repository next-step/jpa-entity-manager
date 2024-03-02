package persistence.entity;

import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<Long, Object> cache;

    public SimplePersistenceContext() {
        this.cache = new HashMap<>();
    }

    @Override
    public Object getEntity(Long id) {
        return cache.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        cache.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Long id = getEntityId(entity);
        cache.remove(id);
    }

    private Long getEntityId(Object entity) {
        Table table = new Table(entity.getClass());

        EntityBinder entityBinder = new EntityBinder(entity);

        PKColumn pkColumn = table.getPKColumn();
        return (Long) entityBinder.getValue(pkColumn);
    }
}
