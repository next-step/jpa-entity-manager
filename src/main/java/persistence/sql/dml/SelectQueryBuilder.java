package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.Table;

public class SelectQueryBuilder {

    public static final String SELECT_FIND_ALL_DEFAULT_DML = "select %s from %s";
    public static final String SELECT_FIND_ID_DEFAULT_DML = "%s where %s";

    public SelectQueryBuilder() {
    }

    public String createFindAllQuery(Class<?> clazz) {
        final Table table = Table.ofClass(clazz);

        return String.format(SELECT_FIND_ALL_DEFAULT_DML, select(table), table.name());
    }

    public String createFindByIdQuery(Class<?> clazz, Long id) {
        final Table table = Table.ofClass(clazz);

        return String.format(SELECT_FIND_ID_DEFAULT_DML, createFindAllQuery(clazz), selectWhere(table.primaryKey(), id));
    }

    private String select(Table table) {
        return String.format("%s, %s", table.primaryKey().name(), String.join(", ", table.columns().names()));
    }

    private String selectWhere(PrimaryKey primaryKey, Long id) {
        return String.format("%s = %dL", primaryKey.name(), id);
    }
}
