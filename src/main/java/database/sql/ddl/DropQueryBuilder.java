package database.sql.ddl;

import database.sql.util.EntityMetadata;

public class DropQueryBuilder {
    private final String tableName;

    public DropQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
    }

    public DropQueryBuilder(Class<?> entityClass) {
        this(new EntityMetadata(entityClass));
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
