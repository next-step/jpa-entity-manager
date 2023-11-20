package persistence.sql.dml;

import persistence.clause.FromClause;
import persistence.clause.WhereClause;
import persistence.common.EntityClazz;
import persistence.common.EntityMetadata;
import persistence.common.FieldClazzList;

import java.util.Iterator;
import java.util.List;

public class DeleteQueryBuilder {

    public static String getQuery(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity);

        StringBuilder sb = new StringBuilder()
                .append("DELETE")
                .append(FromClause.getQuery(entityMetadata.getEntityClazz()));

        String whereClause = WhereClause.getQuery(entityMetadata.getIdFVList());
        sb.append(whereClause);

        return sb.append(";").toString();
    }
}
