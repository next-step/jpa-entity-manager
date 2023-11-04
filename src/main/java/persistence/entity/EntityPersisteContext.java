package persistence.entity;

import java.util.Map;

public class EntityPersisteContext {
    private final Map<Class<?>, EntityPersister> context;

    public EntityPersisteContext(Map<Class<?>, EntityPersister> context) {
        this.context = context;
    }

    public EntityPersister getEntityPersister(Class<?> tClass) {
        return context.get(tClass);
    }

}
