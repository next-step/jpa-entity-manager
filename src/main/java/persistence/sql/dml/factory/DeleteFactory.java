package persistence.sql.dml.factory;

import persistence.sql.dml.clause.Delete;

import java.util.HashMap;

public class DeleteFactory {
    private static final HashMap<Class<?>, Delete> DELETE_MAP = new HashMap<>();

    public static Delete getDelete(Class<?> clazz) {
        Delete delete = DELETE_MAP.get(clazz);
        if (delete == null) {
            delete = new Delete(clazz);
            DELETE_MAP.put(clazz, delete);
        }
        return delete;
    }
}
