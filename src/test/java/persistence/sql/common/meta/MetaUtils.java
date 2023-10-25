package persistence.sql.common.meta;

import persistence.sql.common.instance.Values;

public class MetaUtils {
    public static <T> TableName TableName을_생성함(T t) {
        return TableName을_생성함(t.getClass());
    }

    public static <T> TableName TableName을_생성함(Class<T> tClass) {
        return TableName.of(tClass);
    }

    public static <T> Columns Columns을_생성함(T t) {
        return Columns을_생성함(t.getClass());
    }

    public static <T> Columns Columns을_생성함(Class<T> tClass) {
        return Columns.of(tClass.getDeclaredFields());
    }

    public static <T> Values Values을_생성함(T t) {
        return Values.of(t);
    }
}
