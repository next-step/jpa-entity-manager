package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

public class SelectAllQueryBuilder {
    public String build(Class<?> entityClass) {
        final StringBuilder query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("SELECT * FROM ");
        query.append(tableDefinition.getTableName());
        query.append(";");
        return query.toString();
    }
}
