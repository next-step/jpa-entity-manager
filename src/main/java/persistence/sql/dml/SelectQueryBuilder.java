package persistence.sql.dml;

import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;

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

    public String findByIdQuery(Object entity, Class<?> clazz, Object condition) {
        Field field = new FieldInfos(clazz.getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        return String.format(FIND_BY_ID_QUERY, entityMetaData.getTableInfo().getName(),
                idField.getFieldNameData(), condition);
    }
}
