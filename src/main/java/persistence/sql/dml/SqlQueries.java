package persistence.sql.dml;

import java.util.HashMap;
import java.util.Map;

public class SqlQueries {

    Map<Integer, SqlQuery> sqlQueries;

    public SqlQueries(){
        sqlQueries = new HashMap<>();
    }

    public void addSqlQuery(int code, SqlQuery sqlQuery){
        sqlQueries.put(code, sqlQuery);
    }

    @SuppressWarnings("unchecked")
    public <T extends SqlQuery> T getSqlQuery(int code) {
        return (T) sqlQueries.get(code);
    }

}
