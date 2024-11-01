package persistence.sql.dml;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ColumnValues<T> {

    private final List<ColumnValue> columnValues;

    public ColumnValues(T entity) throws IllegalAccessException {
        this.columnValues = getColumnValues(entity);
    }

    public List<ColumnValue> getValues() {
        return new ArrayList<>(columnValues);
    }

    public boolean hasSameSizeAs(ColumnValues<T> columnValues) {
        return this.columnValues.size() == columnValues.getValues().size();
    }

    public boolean areEqualTo(ColumnValues<T> columnValues) {
        List<ColumnValue> o1 = this.columnValues;
        List<ColumnValue> o2 = columnValues.getValues();
        return IntStream.range(0, o1.size())
            .allMatch(i -> isEqual(o1.get(i), o2.get(i)));
    }

    private boolean isEqual(ColumnValue o1, ColumnValue o2) {
        return Objects.equals(o1.toStringValue(), o2.toStringValue());
    }

    private List<ColumnValue> getColumnValues(T entity) throws IllegalAccessException {
        List<ColumnValue> columnValues = new LinkedList<>();

        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            if (field.isAnnotationPresent(GeneratedValue.class)) {
                continue;
            }

            Object value = field.get(entity);
            columnValues.add(new ColumnValue(field, value));
        }
        return columnValues;
    }

}
