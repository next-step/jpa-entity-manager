package persistence.persister;

import jakarta.persistence.Entity;

public class EntityName {
    private final String name;

    public EntityName(Class<?> clazz) {
        this.name = getEntityName(clazz);
    }

    private String getEntityName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (clazz.isAnnotationPresent(Entity.class)) {
            Entity entity = clazz.getAnnotation(Entity.class);
            if (!entity.name().isEmpty()) {
                name = entity.name();
            }
        }

        return name;
    }

    public String getName() {
        return name;
    }
}
