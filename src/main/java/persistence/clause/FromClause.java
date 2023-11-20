package persistence.clause;

import persistence.common.EntityClazz;

public class FromClause {

    public static String getQuery(EntityClazz entityClazz){
        StringBuilder sb = new StringBuilder(" FROM ");
        sb.append(entityClazz.getName());
        return sb.toString();
    }
}
