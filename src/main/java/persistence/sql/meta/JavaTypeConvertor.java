package persistence.sql.meta;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class JavaTypeConvertor {
    private final Map<Class<?>, Integer> typeRegistry = new HashMap<>();

    public JavaTypeConvertor() {
        addType(String.class, Types.VARCHAR);
        addType(Integer.class, Types.INTEGER);
        addType(Long.class, Types.BIGINT);
    }

    private void addType(Class<?> type, int sqlType) {
        typeRegistry.put(type, sqlType);
    }

    public int getSqlType(Class<?> type) {
        return typeRegistry.get(type);
    }
}
