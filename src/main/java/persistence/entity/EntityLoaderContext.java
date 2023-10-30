package persistence.entity;

import java.util.Map;

public class EntityLoaderContext {
    private final Map<Class<?>, EntityLoader> context;

    public EntityLoaderContext(Map<Class<?>, EntityLoader> context) {
        this.context = context;
    }

    public EntityLoader getEntityLoader(Class<?> tClass) {
        return context.get(tClass);
    }
}
