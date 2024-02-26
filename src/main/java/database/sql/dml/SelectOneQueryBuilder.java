package database.sql.dml;

import database.sql.util.EntityMetadata;

// TODO: 얘를 SelectOne 이 아니라 SelectIdQueryBuilder 로 바꿔볼까?
public class SelectOneQueryBuilder {
    private final String tableName;
    private final String primaryKeyColumnName;
    private final String joinedAllColumnNames;

    public SelectOneQueryBuilder(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        this.tableName = entityMetadata.getTableName();
        this.primaryKeyColumnName = entityMetadata.getPrimaryKeyColumnName();
        this.joinedAllColumnNames = entityMetadata.getJoinedAllColumnNames();
    }

    public String buildQuery(Long id) {
        return String.format("SELECT %s FROM %s WHERE %s = %d",
                             joinedAllColumnNames,
                             tableName,
                             primaryKeyColumnName,
                             id);
    }
}
