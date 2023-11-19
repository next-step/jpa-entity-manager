package persistence.sql.dml;

import persistence.common.EntityClazz;
import persistence.common.EntityMetadata;
import persistence.common.FieldClazzList;

import java.util.Iterator;
import java.util.List;

public class DeleteQueryBuilder {

    public static String getQuery(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        String tableName = entityMetadata.getTableName();

        StringBuilder sb = new StringBuilder()
                .append("DELETE FROM ")
                .append(tableName + " ")
                .append("WHERE ");

        entityMetadata.getIdFVList()
                .forEach(fieldValue -> sb.append(fieldValue.getFieldName())
                        .append("=")
                        .append(fieldValue.getValue()));

        return sb.append(";").toString();
    }
}
