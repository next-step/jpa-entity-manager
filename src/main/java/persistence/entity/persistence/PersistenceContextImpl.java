package persistence.entity.persistence;

import persistence.entity.domain.EntityKey;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> entities = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        return (T) entities.get(new EntityKey(clazz, id));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        entities.put(new EntityKey(entity.getClass(), id), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Columns columns = new Columns(entity.getClass());
        Value value = new Value(columns.getPrimaryKeyColumn(), entity);
        entities.remove(new EntityKey(entity.getClass(), value.getValue()));
    }

}
