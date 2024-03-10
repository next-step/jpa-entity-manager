package persistence.sql.dml.model;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.ColumnUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DMLColumn {

    private static final String SEPARATOR = ", ";

    private final Class<?> clz;

    public DMLColumn(Object entity) {
        this(entity.getClass());
    }

    public DMLColumn(Class<?> clz) {
        this.clz = clz;
    }

    public Field[] getAllFields() {
        return clz.getDeclaredFields();
    }

    public String getIdColumnName() {
        return Arrays.stream(clz.getDeclaredFields())
                .filter(ColumnUtils::isId)
                .findAny()
                .map(java.lang.reflect.Field::getName)
                .orElseThrow(() -> new IllegalArgumentException("ID Column is not exist!"));
    }

    public String getAllColumnClause() {
        final java.lang.reflect.Field[] fields = clz.getDeclaredFields();

        return Arrays.stream(fields)
                .filter(this::includeDatabaseColumn)
                .map(ColumnUtils::name)
                .collect(Collectors.joining(SEPARATOR));
    }

    public boolean includeDatabaseColumn(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public boolean excludeIdColumn(Field field) {
        return !field.isAnnotationPresent(Id.class);
    }
}
