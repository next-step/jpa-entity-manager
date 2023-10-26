package persistence.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityMetadataProvider {

    private final Map<Class<?>, EntityMetadata<?>> cache;

    private EntityMetadataProvider() {
        this.cache = new ConcurrentHashMap<>();
    }

    public static EntityMetadataProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> EntityMetadata<T> getEntityMetadata(final Class<T> clazz) {
        return (EntityMetadata<T>) cache.computeIfAbsent(clazz, EntityMetadata::new);
    }

    private static class InstanceHolder {
        private static final EntityMetadataProvider INSTANCE = new EntityMetadataProvider();
    }

}
