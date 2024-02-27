package persistence.sql.dml.factory;

import persistence.sql.dml.clause.Select;

import java.util.HashMap;
import java.util.Map;

public class SelectFactory {
    private static final Map<Class<?>, Select> SELECT_MAP = new HashMap<>();

    public static Select getSelect(Class<?> clazz) {
        Select select = SELECT_MAP.get(clazz);
        if (select == null) {
            select = new Select(clazz);
            SELECT_MAP.put(clazz, select);
        }
        return select;
    }
}
