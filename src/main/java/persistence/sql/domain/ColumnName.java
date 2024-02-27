package persistence.sql.domain;

import jakarta.persistence.Column;

import java.lang.reflect.Field;

public class ColumnName {

    private final String javaFieldName;

    private final String jdbcColumnName;

    public ColumnName(Field field) {
        this.javaFieldName = field.getName();
        Column annotation = field.getAnnotation(Column.class);
        if (annotation != null && annotation.name().length() > 0) {
            this.jdbcColumnName = annotation.name();
            return;
        }
        this.jdbcColumnName = javaFieldName;
    }

    public String getJavaFieldName() {
        return javaFieldName;
    }

    public String getJdbcColumnName() {
        return jdbcColumnName;
    }
}
