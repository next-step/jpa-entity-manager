package dialect;

import java.util.HashMap;
import java.util.Map;

public abstract class Dialect {

    private final Map<Integer, Class<?>> javaTypeToClassMap = new HashMap<>();
    private final Map<Class<?>, Integer> javaClassToTypeMap = new HashMap<>();

    protected Dialect() {
    }

    protected void registerColumnType(Integer code, Class<?> clazz) {
        javaTypeToClassMap.put(code, clazz);
        javaClassToTypeMap.put(clazz, code);
    }

    public Class<?> getClassByJavaType(Integer javaType) {
        return javaTypeToClassMap.get(javaType);
    }

    public Integer getJavaTypeByClass(Class<?> clazz) {
        return javaClassToTypeMap.get(clazz);
    }

    protected boolean containsJavaType(Integer javaType) {
        return javaTypeToClassMap.containsKey(javaType);
    }

    public abstract String getTypeToStr(Class<?> clazz);

    public abstract void checkJavaType(Integer typeCode);
}
