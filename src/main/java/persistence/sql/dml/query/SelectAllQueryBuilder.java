package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;

public class SelectAllQueryBuilder {
    private final StringBuilder query;

    public SelectAllQueryBuilder(Class<?> entityClass) {
        query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("SELECT * FROM ");
        query.append(tableDefinition.tableName());
        query.append(";");
    }

    public String build() {
        return query.toString();
    }
}
