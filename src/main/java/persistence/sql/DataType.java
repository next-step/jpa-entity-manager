package persistence.sql;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DataType {

    private final Map<Class<?>, Integer> javaTypeAndSqlTypeMap;

    public DataType() {
        javaTypeAndSqlTypeMap = new HashMap<>();
        registerJavaTypeToSqlType();
    }

    private void registerJavaTypeToSqlType() {
        javaTypeAndSqlTypeMap.put(String.class, Types.VARCHAR);
        javaTypeAndSqlTypeMap.put(Integer.class, Types.INTEGER);
        javaTypeAndSqlTypeMap.put(Long.class, Types.BIGINT);
    }

    public int getSqlTypeCode(Class<?> clazz) {
        return javaTypeAndSqlTypeMap.get(clazz);
    }


}
