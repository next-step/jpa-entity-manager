package persistence.persister;

import jakarta.persistence.Table;

public class EntityTableName {

    private final String name;
    public EntityTableName(Class<?> clazz) {
        this.name = getEntityTableName(clazz);
    }

    public String getEntityTableName(Class<?> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        if (clazz.isAnnotationPresent(Table.class)) {
            Table annotation = clazz.getAnnotation(Table.class);
            if (!annotation.name().isEmpty()) {
                name = annotation.name().toLowerCase();
            }
        }

        return name;
    }

    public String getName() {
        return name;
    }
}
