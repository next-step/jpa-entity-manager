package persistence.sql.dml;

import pojo.EntityMetaData;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;

public class DeleteQueryBuilder {

    private static final String DELETE_QUERY = "DELETE %s WHERE %s = %s;";

    private final EntityMetaData entityMetaData;

    public DeleteQueryBuilder(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public String deleteQuery(IdField idField) {
        return String.format(DELETE_QUERY, entityMetaData.getTableInfo().getName(), idField.getFieldNameData(), idField.getFieldValueData());
    }

    public String deleteByIdQuery(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        return deleteQuery(idField);
    }
}
