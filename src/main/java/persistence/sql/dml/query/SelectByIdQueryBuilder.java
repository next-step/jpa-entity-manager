package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

import java.util.StringJoiner;

public class SelectByIdQueryBuilder {

    public String build(Class<?> entityClass, Object id) {
        final TableDefinition tableDefinition = new TableDefinition(entityClass);

        StringBuilder query = new StringBuilder("SELECT ");
        StringJoiner columns = new StringJoiner(", ");

        tableDefinition.withIdColumns().forEach(column -> columns.add(column.getName()));

        query.append(columns);
        query.append(" FROM ").append(tableDefinition.tableName());

        whereClause(query, tableDefinition, id);

        return query.toString();
    }

    private void whereClause(StringBuilder selectQuery, TableDefinition tableDefinition, Object id) {
        selectQuery.append(" WHERE ");
        selectQuery.append(tableDefinition.tableId().getName()).append(" = ");

        if (id instanceof String) {
            selectQuery.append("'").append(id).append("';");
            return;
        }

        selectQuery.append(id).append(";");
    }

}
