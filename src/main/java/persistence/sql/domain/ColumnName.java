package persistence.sql.domain;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColumnName)) return false;
        ColumnName that = (ColumnName) o;
        return Objects.equals(javaFieldName, that.javaFieldName) && Objects.equals(jdbcColumnName, that.jdbcColumnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaFieldName, jdbcColumnName);
    }
}
