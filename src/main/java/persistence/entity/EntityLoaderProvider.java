package persistence.entity;

import java.util.Map;

public class EntityLoaderProvider {

    private final Map<Class<?>, EntityLoader<?>> cache;

    public EntityLoaderProvider(final Map<Class<?>, EntityLoader<?>> loaders) {
        this.cache = loaders;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(final Class<T> clazz) {
        return (EntityLoader<T>) cache.get(clazz);
    }

}
