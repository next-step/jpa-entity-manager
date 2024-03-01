package persistence.sql;

import java.util.HashMap;
import java.util.Map;

public abstract class Dialect extends DataType {
    private final Map<Integer, String> dataTypes = new HashMap<>();

    public String getDataType(final int typeCode) {
        final Integer integer = Integer.valueOf(typeCode);
        final String result = dataTypes.get(integer);
        if (result == null) {
            throw new IllegalArgumentException("No Dialect mapping for type: " + typeCode);
        }
        return result;
    }

    protected void registerColumnType(int code, String name) {
        dataTypes.put(code, name);
    }

    public String getColumnType(Class<?> clazz) {
        return dataTypes.get(getSqlTypeCode(clazz));
    }

}
