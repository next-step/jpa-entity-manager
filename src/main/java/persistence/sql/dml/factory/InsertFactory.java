package persistence.sql.dml.factory;

import persistence.sql.dml.clause.Insert;

import java.util.HashMap;

public class InsertFactory {
    private static final HashMap<Class<?>, Insert> INSERT_MAP = new HashMap<>();

    public static Insert getInsert(Class<?> clazz) {
        Insert insert = INSERT_MAP.get(clazz);
        if (insert == null) {
            insert = new Insert(clazz);
            INSERT_MAP.put(clazz, insert);
        }
        return insert;
    }
}
