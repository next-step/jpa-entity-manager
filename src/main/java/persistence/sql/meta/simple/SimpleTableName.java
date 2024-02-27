package persistence.sql.meta.simple;

import jakarta.persistence.Entity;
import persistence.sql.meta.TableName;

public class SimpleTableName implements TableName {

    private final String name;

    private SimpleTableName(final Class<?> clazz) {
        checkEntityClass(clazz);

        this.name = createTableName(clazz);
    }

    public static SimpleTableName of(final Class<?> clazz) {
        return new SimpleTableName(clazz);
    }

    @Override
    public String name() {
        return this.name;
    }

    private void checkEntityClass(final Class<?> clazz) {
        if (isNotEntityAnnotationPresent(clazz)) {
            throw new IllegalStateException();
        }
    }

    private boolean isNotEntityAnnotationPresent(final Class<?> clazz) {
        return !clazz.isAnnotationPresent(Entity.class);
    }

    private String createTableName(final Class<?> clazz) {
        if (clazz.isAnnotationPresent(jakarta.persistence.Table.class)) {
            return clazz.getAnnotation(jakarta.persistence.Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }
}
