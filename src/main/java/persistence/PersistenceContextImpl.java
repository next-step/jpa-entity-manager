package persistence;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private Map<EntityInfo<?>, Object> entityMap = new HashMap<>();

    @Override
    public Object findEntity(EntityInfo<?> entityObject) {
        return entityMap.get(entityObject);
    }

    @Override
    public void updateEntity(EntityInfo<?> entityObject, Object object) {
        deleteEntity(entityObject);
        this.entityMap.put(entityObject, object);
    }

    @Override
    public void insertEntity(EntityInfo<?> entityObject, Object object) {
        this.entityMap.put(entityObject, object);
    }

    @Override
    public void deleteEntity(EntityInfo<?> entityObject) {
        this.entityMap.remove(entityObject);
    }
}
