package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

import java.io.Serializable;
import java.util.Map;

public class UpdateQueryBuilder {
    private final StringBuilder query;
    private final TableDefinition tableDefinition;
    private final Serializable idValue;

    public UpdateQueryBuilder(Object entity) {
        query = new StringBuilder();
        tableDefinition = new TableDefinition(entity.getClass());
        idValue = tableDefinition.tableId().hasValue(entity) ? tableDefinition.tableId().getValue(entity) : null;

        query.append("UPDATE ");
        query.append(tableDefinition.tableName());
    }

    public UpdateQueryBuilder columns(Map<String, Object> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns cannot be null or empty");
        }

        query.append(" SET ");
        String columnClause = columns.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + entry.getValue())
                .reduce((column1, column2) -> column1 + ", " + column2).orElse("");
        query.append(columnClause);
        return this;
    }

    public String build() {
        if (idValue == null) {
            throw new IllegalArgumentException("Entity must have an ID");
        }

        if (!query.toString().contains("SET")) {
            throw new IllegalArgumentException("Columns must be set");
        }

        query.append(" WHERE ");
        query.append(tableDefinition.tableId().getName()).append(" = ");
        query.append(idValue);
        query.append(";");

        return query.toString();
    }
}
