package persistence.dialect;

import java.util.concurrent.ConcurrentHashMap;

public class TypeJavaClassMappings {
    public static final TypeJavaClassMappings INSTANCE = new TypeJavaClassMappings();

    private final ConcurrentHashMap<Class<?>, Integer> javaClassToJdbcTypeCodeMap;

    public TypeJavaClassMappings() {
        this.javaClassToJdbcTypeCodeMap = buildJavaClassToJdbcTypeCodeMappings();
    }

    private static ConcurrentHashMap<Class<?>, Integer> buildJavaClassToJdbcTypeCodeMappings() {
        final ConcurrentHashMap<Class<?>, Integer> workMap = new ConcurrentHashMap<>();

        workMap.put(String.class, SqlTypes.VARCHAR);
        workMap.put(Integer.class, SqlTypes.INTEGER);
        workMap.put(Long.class, SqlTypes.BIGINT);

        return workMap;
    }

    public int valueOf(Class<?> type) {
        return javaClassToJdbcTypeCodeMap.get(type);
    }
}
