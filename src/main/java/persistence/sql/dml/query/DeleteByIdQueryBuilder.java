package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

public class DeleteByIdQueryBuilder {
    private final StringBuilder query;

    public DeleteByIdQueryBuilder(Object entity) {
        query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entity.getClass());
        final TableId tableId = tableDefinition.tableId();
        final Object idValue = tableId.getValue(entity);

        query.append("DELETE FROM ");
        query.append(tableDefinition.tableName());
        query.append(" WHERE ");
        query.append(tableId.getName()).append(" = ");
        query.append(idValue).append(";");
    }

    public String build() {
        return query.toString();
    }
}
