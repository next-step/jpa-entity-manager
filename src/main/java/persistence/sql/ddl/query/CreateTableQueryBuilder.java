package persistence.sql.ddl.query;

import persistence.sql.Dialect;
import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

public class CreateTableQueryBuilder {
    private final StringBuilder query;

    public CreateTableQueryBuilder(Dialect dialect, Class<?> entityClass) {
        this.query = new StringBuilder();

        TableDefinition tableDefinition = new TableDefinition(entityClass);

        query.append("CREATE TABLE ").append(tableDefinition.getTableName());
        query.append(" (");

        tableDefinition.withIdColumns().forEach(column -> column.applyToCreateTableQuery(query, dialect));

        definePrimaryKey(tableDefinition.getTableId(), query);

        query.append(");");
    }

    public String build() {
        return query.toString();
    }

    private void definePrimaryKey(TableId pk, StringBuilder query) {
        query.append("PRIMARY KEY (").append(pk.getColumnName()).append(")");
    }
}
