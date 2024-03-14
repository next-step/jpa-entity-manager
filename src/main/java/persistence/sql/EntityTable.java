package persistence.sql;

import static persistence.sql.ddl.common.StringConstants.SCHEMA_TABLE_DELIMITER;

import jakarta.persistence.Table;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.sql.ddl.common.StringConstants;

public class EntityTable {
    private final Class<?> entityClass;

    public EntityTable(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getFullTableName() {
        return Stream.of(
                getSchemaName(),
                getTableName()
            )
            .filter(s -> !s.isBlank())
            .collect(Collectors.joining(SCHEMA_TABLE_DELIMITER));
    }

    public String getSchemaName() {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return getSchemaNameFrom(table);
        }

        return StringConstants.EMPTY_STRING;
    }

    public String getTableName() {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return getTableNameFrom(table);
        }

        return entityClass.getSimpleName();
    }

    private String getSchemaNameFrom(Table table) {
        if (!table.schema().isEmpty()) {
            return table.schema();
        }

        return StringConstants.EMPTY_STRING;
    }

    private String getTableNameFrom(Table table) {
        if (!table.name().isEmpty()) {
            return table.name();
        }

        return StringConstants.EMPTY_STRING;
    }
}
