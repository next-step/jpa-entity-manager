package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

import java.util.StringJoiner;

public class SelectByIdQueryBuilder {
    private final StringBuilder query;

    public SelectByIdQueryBuilder(Class<?> entityClass, Object id) {
        query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("SELECT ");
        StringJoiner columns = new StringJoiner(", ");

        tableDefinition.withIdColumns().forEach(column -> columns.add(column.getName()));

        query.append(columns);
        query.append(" FROM ").append(tableDefinition.getTableName());

        whereClause(query, tableDefinition, id);
    }

    public String build() {
        return query.toString();
    }

    private void whereClause(StringBuilder selectQuery, TableDefinition tableDefinition, Object id) {
        selectQuery.append(" WHERE ");
        selectQuery.append(tableDefinition.getTableId().getName()).append(" = ");

        if (id instanceof String) {
            selectQuery.append("'").append(id).append("';");
            return;
        }

        selectQuery.append(id).append(";");
    }

}
