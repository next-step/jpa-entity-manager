package persistence.sql.entity;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityColumns {
    private final List<EntityColumn> columns;

    public EntityColumns(List<EntityColumn> columns) {
        this.columns = columns;
    }

    public static EntityColumns from(Class<?> clazz) {
        List<EntityColumn> collect = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(EntityColumn::from)
                .collect(Collectors.toList());
        return new EntityColumns(collect);
    }

    public List<EntityColumn> getColumns() {
        return columns;
    }

    public EntityColumn getEntityColumn(Field field) {
        return columns.stream()
                .filter(entityColumn -> entityColumn.getColumnName().equals(field.getName()))
                .findFirst()
                .orElse(null);
    }


}
