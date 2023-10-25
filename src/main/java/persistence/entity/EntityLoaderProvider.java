package persistence.entity;

import persistence.core.PersistenceEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLoaderProvider {

    private final Map<Class<?>, EntityLoader<?>> cache;
    private final PersistenceEnvironment persistenceEnvironment;
    private final EntityPersisterProvider entityPersisterProvider;

    public EntityLoaderProvider(final PersistenceEnvironment persistenceEnvironment, final EntityPersisterProvider entityPersisterProvider) {
        this.persistenceEnvironment = persistenceEnvironment;
        this.entityPersisterProvider = entityPersisterProvider;
        this.cache = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(final Class<T> clazz) {
        return (EntityLoader<T>) cache.computeIfAbsent(clazz, cls ->
                new EntityLoader<T>(clazz, persistenceEnvironment, entityPersisterProvider.getEntityPersister(clazz))
        );
    }

}
