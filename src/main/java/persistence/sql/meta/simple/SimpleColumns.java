package persistence.sql.meta.simple;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class SimpleColumns implements Columns {

    private final List<Column> columns;

    private SimpleColumns(final Class<?> clazz) {
        this.columns = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isNotIdField)
                .map(SimpleColumn::new)
                .collect(Collectors.toList());
    }

    public static SimpleColumns of(Class<?> clazz) {
        return new SimpleColumns(clazz);
    }

    @Override
    public List<String> names() {
        return this.columns.stream()
                .map(Column::getFieldName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> values(Object object) {
        return this.columns.stream()
                .map(c -> c.value(object))
                .collect(Collectors.toList());
    }

    @Override
    public List<Column> getColumns() {
        return columns;
    }

    private boolean isNotIdField(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}
