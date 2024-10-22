package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

public class DeleteByIdQueryBuilder {
    private final StringBuilder query;

    public DeleteByIdQueryBuilder(Object entity) {
        query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entity.getClass());
        final TableId tableId = tableDefinition.tableId();

        if (!tableId.hasValue(entity)) {
            throw new IllegalArgumentException("Entity does not have an ID value");
        }
        final Object idValue = tableId.getValueAsString(entity);

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
