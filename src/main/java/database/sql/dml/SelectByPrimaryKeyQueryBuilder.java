package database.sql.dml;

import database.sql.util.EntityMetadata;

public class SelectByPrimaryKeyQueryBuilder {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final String joinedAllColumnNames;

    public SelectByPrimaryKeyQueryBuilder(EntityMetadata entityMetadata) {
        this.tableName = entityMetadata.getTableName();
        this.primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public SelectByPrimaryKeyQueryBuilder(Class<?> entityClass) {
        this(new EntityMetadata(entityClass));
    }

    public String buildQuery(Long id) {
        return String.format("SELECT %s FROM %s WHERE %s = %d",
                             joinedAllColumnNames,
                             tableName,
                             primaryKeyColumnName,
                             id);
    }
}
