package persistence.sql.meta;

import jakarta.persistence.Table;

import java.util.Objects;

public class TableName {
    private final String name;

    public TableName(Class<?> entityType) {
        this.name = value(entityType);
    }

    public String value() {
        return name;
    }

    private String value(Class<?> entityType) {
        final Table table = entityType.getAnnotation(Table.class);
        if (Objects.nonNull(table) && Objects.nonNull(table.name()) && !table.name().isBlank()) {
            return table.name();
        }
        return entityType.getSimpleName()
                .toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableName tableName = (TableName) o;
        return Objects.equals(name, tableName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
