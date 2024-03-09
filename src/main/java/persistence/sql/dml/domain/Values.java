package persistence.sql.dml.domain;

import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Values {

    private final List<Value> values;

    public Values(Value... value) {
        this.values = List.of(value);
    }

    public Values(Columns columns, Object entity) {
        this.values = createValues(columns, entity);
    }

    public Values(Class<?> clazz, List<String> whereColumnNames, List<Object> whereValues) {
        this.values = createValues(clazz, whereColumnNames, whereValues);
    }

    private List<Value> createValues(Columns columns, Object entity) {
        return columns.getColumns().stream()
                .map(column -> new Value(column, entity))
                .collect(Collectors.toList());
    }

    private List<Value> createValues(Class<?> clazz, List<String> whereColumnNames, List<Object> whereValues) {
        return IntStream.range(0, whereColumnNames.size())
                .mapToObj(index -> createValue(clazz, whereColumnNames.get(index), whereValues.get(index)))
                .collect(Collectors.toList());
    }

    private Value createValue(Class<?> clazz, String columnName, Object value) {
        try {
            Field field = clazz.getDeclaredField(columnName);
            return new Value(Column.from(field), field.getType(), value);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Value> getValues() {
        return values;
    }

}
