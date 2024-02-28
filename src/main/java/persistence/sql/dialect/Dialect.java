package persistence.sql.dialect;

import jakarta.persistence.GenerationType;

import java.util.HashMap;
import java.util.Map;

public abstract class Dialect {
    private final Map<Integer, String> columnTypeMap = new HashMap<>();

    protected void registerColumnType(int code, String name) {
        columnTypeMap.put(code, name);
    }

    public String getColumnType(final int typeCode) {
        final Integer integer = typeCode;
        final String result = columnTypeMap.get(integer);
        if (result == null) {
            throw new IllegalArgumentException("No Dialect mapping for type: " + typeCode);
        }

        return result;
    }

    public abstract String generatorCreatePrimaryDdl(final GenerationType strategy);

    public abstract String getNotNullConstraint(boolean nullable);
}
