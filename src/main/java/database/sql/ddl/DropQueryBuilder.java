package database.sql.ddl;

import database.sql.util.EntityMetadata;

public class DropQueryBuilder {
    private final String tableName;

    public DropQueryBuilder(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
