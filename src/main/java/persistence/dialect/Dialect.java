package persistence.dialect;

import java.util.HashMap;
import java.util.Map;

public abstract class Dialect {
    private final Map<Integer, String> typeRegistry;

    protected Dialect() {
        this.typeRegistry = new HashMap<>();
    }

    protected void addType(int sqlType, String dbType) {
        typeRegistry.put(sqlType, dbType);
    }

    public String getDbTypeName(int sqlType) {
        return typeRegistry.get(sqlType);
    }
}
