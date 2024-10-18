package persistence.sql.ddl;

import persistence.sql.meta.EntityTable;

public class DropQueryBuilder {
    private static final String QUERY_TEMPLATE = "DROP TABLE IF EXISTS %s";

    private final EntityTable entityTable;

    public DropQueryBuilder(Class<?> entityType) {
        this.entityTable = new EntityTable(entityType);
    }

    public String drop() {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName());
    }
}
