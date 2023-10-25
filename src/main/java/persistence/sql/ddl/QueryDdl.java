package persistence.sql.ddl;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class QueryDdl {
    public static String create(TableName tableName, Columns columns) {
        return CreateQuery.of(tableName, columns);
    }

    public static <T> String drop(Class<T> tClass) {
        return DropQuery.drop(tClass);
    }
}
