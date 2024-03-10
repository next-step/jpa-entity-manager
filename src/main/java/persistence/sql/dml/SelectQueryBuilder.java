package persistence.sql.dml;

import pojo.EntityMetaData;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldName;

public class SelectQueryBuilder {

    private static final String FIND_ALL_QUERY = "SELECT * FROM %s;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM %s WHERE %s = %s;";

    private final EntityMetaData entityMetaData;

    public SelectQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String findAllQuery() {
        return String.format(FIND_ALL_QUERY, entityMetaData.getTableInfo().getName());
    }

    public String findByIdQuery(Class<?> clazz, Object condition) {
        FieldInfo idFieldInfo = new FieldInfos(clazz.getDeclaredFields()).getIdFieldData();
        return String.format(FIND_BY_ID_QUERY, entityMetaData.getTableInfo().getName(),
                new FieldName(idFieldInfo.getField()).getName(), condition);
    }
}
