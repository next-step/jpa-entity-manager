package jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityInfo<?>, Object> entityMap = new HashMap<>();

    public PersistenceContextImpl() {
    }

    @Override
    public void add(EntityInfo<?> entityInfo, Object entity) {
        if (entityMap.containsKey(entityInfo)) {
            return;
        }
        entityMap.put(entityInfo, entity);
    }

    @Override
    public Object get(EntityInfo<?> entityInfo) {
        return entityMap.get(entityInfo);
    }

    @Override
    public void remove(EntityInfo<?> entityInfo) {
        entityMap.remove(entityInfo);
    }

    @Override
    public boolean contain(EntityInfo<?> entityInfo) {
        return entityMap.containsKey(entityInfo);
    }

}
