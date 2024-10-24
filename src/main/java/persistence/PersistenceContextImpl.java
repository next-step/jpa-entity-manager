package persistence;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey<?>, Object> entityMap = new HashMap<>();

    @Override
    public Object findEntity(EntityKey<?> entityObject) {
        return entityMap.get(entityObject);
    }

    @Override
    public void insertEntity(EntityKey<?> entityObject, Object object) {
        this.entityMap.put(entityObject, object);
    }

    @Override
    public void deleteEntity(EntityKey<?> entityObject) {
        this.entityMap.remove(entityObject);
    }
}
