package persistence.sql.meta;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EntityColumns {
    private final List<EntityColumn> entityColumns;

    private EntityColumns(final Class<?> clazz) {
        this.entityColumns = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isNotIdField)
                .map(EntityColumn::new)
                .collect(Collectors.toList());
    }

    public static EntityColumns of(Class<?> clazz) {
        return new EntityColumns(clazz);
    }

    public List<String> names() {
        return this.entityColumns.stream()
                .map(EntityColumn::getFieldName)
                .collect(Collectors.toList());
    }

    public List<String> values(Object object) {
        return this.entityColumns.stream()
                .map(c -> c.value(object))
                .collect(Collectors.toList());
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns;
    }

    private boolean isNotIdField(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}
