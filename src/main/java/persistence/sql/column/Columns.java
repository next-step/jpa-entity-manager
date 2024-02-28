package persistence.sql.column;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Columns {
    private static final String COMMA = ", ";

    private final List<GeneralColumn> values;

    public Columns(Object object, Dialect dialect) {
        Field[] fields = object.getClass().getDeclaredFields();
        this.values = createGeneralColumns(fields, (field) -> new GeneralColumn(object, field, dialect));
    }

    public Columns(Field[] fields, Dialect dialect) {
        this.values = createGeneralColumns(fields, (field) -> new GeneralColumn(field, dialect));
    }

    private List<GeneralColumn> createGeneralColumns(Field[] fields, Function<Field, GeneralColumn> columnCreator) {
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .map(columnCreator)
                .collect(Collectors.toList());
    }

    public String getColumnsDefinition() {
        return this.values
                .stream()
                .map(Column::getDefinition)
                .collect(Collectors.joining(COMMA));
    }

    public String getColumnNames() {
        return this.values
                .stream()
                .map(Column::getName)
                .collect(Collectors.joining(COMMA));
    }

    public List<GeneralColumn> getValues() {
        return Collections.unmodifiableList(values);
    }

    public String getColumnValues() {
        return this.values.stream()
                .map(GeneralColumn::getValue)
                .map(String::valueOf)
                .collect(Collectors.joining(COMMA));
    }
}