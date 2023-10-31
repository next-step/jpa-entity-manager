package persistence.entity;

import java.util.Map;

public class EntityPersisterProvider {
    private final Map<Class<?>, EntityPersister> cache;

    public EntityPersisterProvider(final Map<Class<?>, EntityPersister> persisters) {
        this.cache = persisters;
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.get(clazz);
    }

}
