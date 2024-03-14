package persistence.sql.ddl.clause.column;

import java.lang.reflect.Field;

public class ColumnSpec {

    private final String name;
    private final Class<?> type;

    public ColumnSpec(Field field) {
        this.name = initName(field);
        this.type = field.getType();
    }

    public String name() {
        return name;
    }

    public Class<?> type() {
        return type;
    }

    private String initName(Field field) {
        jakarta.persistence.Column column = field.getAnnotation(jakarta.persistence.Column.class);
        if (column == null) {
            return field.getName();
        }
        if (column.name().isEmpty()) {
            return field.getName();
        }
        return column.name();
    }
}
