package persistence.sql.dml;

import persistence.clause.SetClause;
import persistence.clause.WhereClause;
import persistence.common.EntityMetadata;
import persistence.common.FieldValue;

import java.util.List;

public class UpdateQueryBuilder {

    public static String getQuery(Object entity){
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        StringBuilder sb = new StringBuilder("UPDATE");
        List<FieldValue> attributeFVList = entityMetadata.getAttributeFVList();

        String setClause = SetClause.getQuery(attributeFVList);
        sb.append(setClause);

        List<FieldValue> idFVList = entityMetadata.getIdFVList();
        String whereClause = WhereClause.getQuery(idFVList);
        sb.append(whereClause);
        return sb.append(";").toString();
    }
}
