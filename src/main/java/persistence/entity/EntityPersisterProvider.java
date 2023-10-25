package persistence.entity;

import persistence.core.PersistenceEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersisterProvider {

    private final Map<Class<?>, EntityPersister> cache;
    private final PersistenceEnvironment persistenceEnvironment;

    public EntityPersisterProvider(final PersistenceEnvironment persistenceEnvironment) {
        this.persistenceEnvironment = persistenceEnvironment;
        this.cache = new ConcurrentHashMap<>();
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.computeIfAbsent(clazz, cls->
            new EntityPersister(clazz, persistenceEnvironment)
        );
    }

}
