package persistence.persistencecontext;

import persistence.sql.domain.Table;
import utils.ValueExtractor;

import java.util.HashMap;
import java.util.Map;

public class MyPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> entities = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        return (T) entities.get(new EntityKey(id, clazz));
    }

    @Override
    public void addEntity(Object entity) {
        Table table = Table.from(entity.getClass());
        Object id = ValueExtractor.extract(entity, table.getIdColumn());
        entities.put(new EntityKey(id, entity.getClass()), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Table table = Table.from(entity.getClass());
        Object id = ValueExtractor.extract(entity, table.getIdColumn());
        EntityKey entityKey = new EntityKey(id, entity.getClass());
        entities.remove(entityKey);
    }
}
