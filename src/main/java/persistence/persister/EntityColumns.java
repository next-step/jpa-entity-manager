package persistence.persister;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.TableSQLMapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class EntityColumns {
    private final Field[] columnFields;

    public EntityColumns(Class<?> clazz) {
        columnFields = Arrays
            .stream(clazz.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .filter(field -> !field.isAnnotationPresent(Id.class))
            .peek(field -> field.setAccessible(true))
            .toArray(Field[]::new);
    }

    public String[] getColumnNames() {
        return  Arrays
            .stream(columnFields)
            .map(TableSQLMapper::getColumnName)
            .toArray(String[]::new);
    }

    public Field getColumnField(String columnName) {
        return Arrays
            .stream(columnFields)
            .filter(field -> Objects.equals(TableSQLMapper.getColumnName(field), columnName))
            .findFirst()
            .orElse(null);
    }

    public Object[] getColumnValues(Object object) {
        return Arrays
            .stream(columnFields)
            .map(field -> TableSQLMapper.getColumnValue(field, object))
            .toArray();
    }
}
