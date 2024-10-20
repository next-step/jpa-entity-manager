package persistence.sql.dml;

import persistence.sql.meta.EntityTable;

public class DeleteQueryBuilder {
    private static final String QUERY_TEMPLATE = "DELETE FROM %s WHERE %s";

    private final EntityTable entityTable;

    public DeleteQueryBuilder(Object entity) {
        this.entityTable = new EntityTable(entity);
    }

    public String delete() {
        final Object id = entityTable.getIdValue();
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), entityTable.getWhereClause(id));
    }
}
