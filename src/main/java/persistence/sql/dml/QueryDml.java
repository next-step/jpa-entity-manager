package persistence.sql.dml;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class QueryDml {

    public static <T> String insert(TableName tableName, Columns columns, T t) {
        return InsertQuery.create(tableName, columns, t);
    }

    public static String select(String methodName, TableName tableName, Columns columns, Object... args) {
        return SelectQuery.create(methodName, tableName, columns, args);
    }

    public static String delete(TableName tableName, Columns columns, Object args) {
        return DeleteQuery.create(tableName, columns, args);
    }

    public static <T> String update(T t, TableName tableName, Columns columns, Object arg) {
        return UpdateQuery.create(t, tableName, columns, arg);
    }
}
