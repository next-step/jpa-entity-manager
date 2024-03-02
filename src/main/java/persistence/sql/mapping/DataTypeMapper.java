package persistence.sql.mapping;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DataTypeMapper {
    private final Map<Class<?>, Integer> javaToSqlType = new HashMap<>();
    private final Map<Integer, Class<?>> sqlTypeToJava = new HashMap<>();

    public DataTypeMapper() {
        addMapping(Long.class, Types.BIGINT);
        addMapping(Integer.class, Types.INTEGER);
        addMapping(String.class, Types.VARCHAR);
    }

    private void addMapping(Class<?> javaType, int sqlType) {
        javaToSqlType.put(javaType, sqlType);
        sqlTypeToJava.put(sqlType, javaType);
    }

    public int mapToSqlType(Class<?> type) {
        Integer code = javaToSqlType.get(type);
        if(code == null) {
            throw new IllegalArgumentException(type + "지원하지 않는 타입입니다.");
        }
        return code;
    }
}
