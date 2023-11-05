package persistence.persister;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.TableSQLMapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityColumns {
    private final Field[] columnFields;
    private final Map<Field, Object> columnToValues = new HashMap<>();
    private final Map<String, Field> columnNameToFields = new HashMap<>();

    public EntityColumns(Class<?> clazz) {
        columnFields = Arrays
            .stream(clazz.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .filter(field -> !field.isAnnotationPresent(Id.class))
            .peek(field -> field.setAccessible(true))
            .peek(field -> columnToValues.put(field, null))
            .peek(field-> columnNameToFields.put(TableSQLMapper.getColumnName(field), field))
            .toArray(Field[]::new);
    }

    public String[] getColumnNames() {
        final List<String> columnNames = Arrays
            .stream(columnFields)
            .map(TableSQLMapper::getColumnName)
            .collect(Collectors.toList());

        return columnNames.toArray(String[]::new);
    }

    public Field getField(String columnName) {
        return this.columnNameToFields.get(columnName);
    }

    public Object[] getColumnValues(Object object) {
        final List<Object> columnNames = Arrays
            .stream(columnFields)
            .map(field -> {
                Object value = TableSQLMapper.getColumnValue(field, object);
                columnToValues.put(field, value);
                return value;
            })
            .collect(Collectors.toList());

        return columnNames.toArray(Object[]::new);
    }
}
