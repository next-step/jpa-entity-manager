package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityPersisterCachePool {
    private final Map<Class<?>, EntityPersister<Object>> pool;

    public EntityPersisterCachePool() {
        this.pool = new HashMap<>();
    }

    public EntityPersister<Object> lookup(Class<?> entityClass) {
        return pool.get(entityClass);
    }

    public void register(Class<?> entityClass, EntityPersister<Object> entityPersister) {
        pool.put(entityClass, entityPersister);
    }
}
