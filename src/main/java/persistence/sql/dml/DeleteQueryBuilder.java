package persistence.sql.dml;

import dialect.Dialect;
import pojo.EntityMetaData;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldName;
import pojo.FieldValue;

import java.lang.reflect.Field;

public class DeleteQueryBuilder {

    private static final String DELETE_QUERY = "DELETE %s WHERE %s = %s;";

    private final Dialect dialect;
    private final EntityMetaData entityMetaData;

    public DeleteQueryBuilder(Dialect dialect, EntityMetaData entityMetaData) {
        this.dialect = dialect;
        this.entityMetaData = entityMetaData;
    }

    public String deleteQuery(Field field, Object object) {
        FieldName fieldName = new FieldName(field);
        FieldValue fieldValue = new FieldValue(dialect, field, object);
        return String.format(DELETE_QUERY, entityMetaData.getTableInfo().getName(), fieldName.getName(), fieldValue.getValue());
    }

    public String deleteByIdQuery(Object object) {
        FieldInfo idFieldInfo = new FieldInfos(object.getClass().getDeclaredFields()).getIdFieldData();
        return deleteQuery(idFieldInfo.getField(), object);
    }
}
