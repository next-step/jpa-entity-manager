package persistence.sql.dml;

import persistence.clause.FromClause;
import persistence.clause.WhereClause;
import persistence.common.EntityClazz;
import persistence.common.EntityMetadata;
import persistence.common.FieldClazzList;
import persistence.common.FieldValue;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {

    private static String SELECT_PREFIX= "SELECT *";

    public String findAll(Class<?> clazz) {
        EntityClazz entityClazz = new EntityClazz(clazz);
        StringBuilder sb = new StringBuilder()
                .append(SELECT_PREFIX)
                .append(FromClause.getQuery(entityClazz));

        return sb.append(";").toString();
    }

    public String findById(Class<?> clazz, List<Object> ids) {
        EntityClazz entityClazz = new EntityClazz(clazz);
        FieldClazzList fieldClazzList = new FieldClazzList(clazz);

        StringBuilder sb = new StringBuilder()
                .append(SELECT_PREFIX)
                .append(FromClause.getQuery(entityClazz));

        Iterator<Object> idIter = ids.iterator();
        String whereClause = WhereClause.getQuery(fieldClazzList.getIdFieldList()
                .stream().map(fc -> new FieldValue(fc, idIter.next().toString()))
                .collect(Collectors.toList()));

        sb.append(whereClause);
        return sb.append(";").toString();
    }

    public String find(Object entity){
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        StringBuilder sb = new StringBuilder()
                .append(SELECT_PREFIX)
                .append(FromClause.getQuery(entityMetadata.getEntityClazz()));

        sb.append(entityMetadata.getTableName());

        String whereClause = WhereClause.getQuery(entityMetadata.getIdFVList());
        sb.append(whereClause);

        return sb.append(";").toString();
    }

}
