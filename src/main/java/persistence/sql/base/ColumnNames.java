package persistence.sql.base;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColumnNames {
    private static final String DELIMITER = ", ";
    private final List<ColumnName> columnNames;

    private ColumnNames(List<ColumnName> columnNames) {
        this.columnNames = Collections.unmodifiableList(columnNames);
    }

    public static ColumnNames of(List<Field> fields) {
        return fields.stream()
                .map(ColumnName::of)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ColumnNames::new));
    }

    public String names() {
        return columnNames.stream()
                .map(ColumnName::name)
                .collect(Collectors.joining(DELIMITER));
    }
}
