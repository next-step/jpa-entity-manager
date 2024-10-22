package persistence.sql.dml.query;

import persistence.sql.definition.TableDefinition;
import persistence.sql.definition.TableId;

public class DeleteByIdQueryBuilder {

    public String build(Object entity) {
        final StringBuilder query = new StringBuilder();
        final TableDefinition tableDefinition = new TableDefinition(entity.getClass());
        final TableId tableId = tableDefinition.getTableId();

        final Object idValue = tableDefinition.getIdValue(entity);

        query.append("DELETE FROM ");
        query.append(tableDefinition.getTableName());
        query.append(" WHERE ");
        query.append(tableId.getColumnName()).append(" = ");
        query.append(idValue).append(";");
        return query.toString();
    }
}
