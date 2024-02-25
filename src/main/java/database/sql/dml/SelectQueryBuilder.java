package database.sql.dml;

import database.sql.util.EntityMetadata;

public class SelectQueryBuilder {
    private final String tableName;
    private final String joinedAllColumnNames;

    public SelectQueryBuilder(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public String buildQuery() {
        return String.format("SELECT %s FROM %s", joinedAllColumnNames, tableName);
    }
}
