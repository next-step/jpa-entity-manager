package persistence.sql.ddl.domain;

import jakarta.persistence.Transient;
import persistence.sql.dml.domain.Value;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Columns {

    private final List<Column> columns;

    public Columns(Class<?> clazz) {
        this.columns = createColumns(clazz);
    }

    private List<Column> createColumns(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientAnnotationPresent)
                .map(Column::from)
                .collect(Collectors.toList());
    }

    private boolean isNotTransientAnnotationPresent(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public List<Column> getColumns() {
        return columns;
    }

    public Column getPrimaryKeyColumn() {
        return columns.stream()
                .filter(Column::isPrimaryKey)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Primary key not found."));
    }

    public Object getOriginValue(Object entity) {
        return new Value(getPrimaryKeyColumn(), entity).getOriginValue();
    }

    public void setPkValue(Object entity, Object id) {
        getPrimaryKeyColumn().setFieldValue(entity, id);
    }

}
