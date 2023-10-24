package persistence.sql.dml;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class QueryDml {

    public static <T> String insert(TableName tableName, Columns columns, T t) {
        return InsertQuery.create(tableName, columns, t);
    }

    public static <T> String select(Class<T> tClass, String methodName) {
        return SelectQuery.create(tClass, methodName);
    }

    public static <T> String select(Class<T> tClass, String methodName, Object... args) {
        return SelectQuery.create(tClass, methodName, args);
    }

    public static <T> String delete(T t, Object args) {
        return DeleteQuery.create(t, args);
    }

    public static <T> String delete(T t) {
        return DeleteQuery.create(t);
    }

    public static <T> String update(T t, Object arg) {
        return UpdateQuery.create(t, arg);
    }
}
