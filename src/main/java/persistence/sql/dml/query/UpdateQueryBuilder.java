package persistence.sql.dml.query;

import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.util.List;

public class UpdateQueryBuilder {
    private final StringBuilder query;

    public UpdateQueryBuilder(Object entity) {
        query = new StringBuilder();
        final Class<?> entityClass = entity.getClass();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);
        final List<? extends Queryable> targetColumns = tableDefinition.withIdColumns();

        query.append("UPDATE ");
        query.append(tableDefinition.tableName());

        query.append(" SET ");
        String columnClause = columnClause(tableDefinition, entity, query);
        query.append(columnClause);

        query.append(" WHERE ");
        query.append(tableDefinition.tableId().getName()).append(" = ");
        query.append(tableDefinition.tableId().getValue(entity));
        query.append(";");
    }

    public String build() {
        return query.toString();
    }

    private static String columnClause(TableDefinition tableDefinition, Object entity, StringBuilder query) {
        return tableDefinition.withoutIdColumns().stream()
                .map(column -> {
                    final String columnValue = column.hasValue(entity) ? column.getValue(entity) : "null";
                    return column.getName() + " = " + columnValue;
                }).reduce((column1, column2) -> column1 + ", " + column2).orElse("");
    }

}
