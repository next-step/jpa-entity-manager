package persistence.sql.meta.simple;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleEntityMetaCreator implements EntityMetaCreator {

    private final Class<?> clazz;

    private SimpleEntityMetaCreator(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static SimpleEntityMetaCreator of(Class<?> clazz) {
        return new SimpleEntityMetaCreator(clazz);
    }

    @Override
    public String createTableName() {
        checkEntityClass(clazz);

        return createTableName(clazz);
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

    @Override
    public PrimaryKey createPrimaryKey() {
        final SimpleColumn simpleColumn = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isIdField)
                .findFirst()
                .map(SimpleColumn::new)
                .orElseThrow(IllegalArgumentException::new);
        return new SimplePrimaryKey(simpleColumn);
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    @Override
    public Columns createColumns() {
        final List<SimpleColumn> simpleColumns = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isNotIdField)
                .map(SimpleColumn::new)
                .collect(Collectors.toList());

        return new SimpleColumns(simpleColumns);
    }

    private boolean isNotIdField(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}

