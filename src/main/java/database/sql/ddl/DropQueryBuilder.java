package database.sql.ddl;

import database.mapping.EntityMetadata;

public class DropQueryBuilder {
    private final String tableName;

    public DropQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
    }

    public DropQueryBuilder(Class<?> clazz) {
        this(EntityMetadata.fromClass(clazz));
    }

    public String buildQuery() {
        return String.format("DROP TABLE %s", tableName);
    }
}
