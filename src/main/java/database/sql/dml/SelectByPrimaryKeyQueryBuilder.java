package database.sql.dml;

import database.mapping.EntityMetadata;

public class SelectByPrimaryKeyQueryBuilder {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final String joinedAllColumnNames;

    public SelectByPrimaryKeyQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public SelectByPrimaryKeyQueryBuilder(Class<?> clazz) {
        this(EntityMetadata.fromClass(clazz));
    }

    public String buildQuery(Long id) {
        return String.format("SELECT %s FROM %s WHERE %s = %d",
                             joinedAllColumnNames,
                             tableName,
                             primaryKeyColumnName,
                             id);
    }
}
