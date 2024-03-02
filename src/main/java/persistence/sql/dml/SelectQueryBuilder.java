package persistence.sql.dml;

import domain.EntityMetaData;

public class SelectQueryBuilder {

    private static final String FIND_ALL_QUERY = "SELECT * FROM %s;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM %s WHERE %s = %s;";

    private final EntityMetaData entityMetaData;

    public SelectQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String findAllQuery() {
        return String.format(FIND_ALL_QUERY, entityMetaData.getTableName());
    }

    public String findByIdQuery(Object condition) {
        return String.format(FIND_BY_ID_QUERY, entityMetaData.getTableName(), entityMetaData.getIdField(), condition);
    }
}
