package persistence.sql.dml.builder;

import persistence.sql.base.ColumnName;
import persistence.sql.dml.column.ColumnValue;
import persistence.sql.dml.column.DmlColumn;

import static persistence.sql.dml.statement.QueryStatement.selectFrom;

public class SelectQueryBuilder {
    public static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();

    private SelectQueryBuilder() {
    }

    public String findAll(Class<?> clazz) {
        return selectFrom(clazz).query();
    }

    public String findById(Class<?> clazz, Object id) {
        return selectFrom(clazz)
                .where(new DmlColumn(ColumnName.id(clazz), new ColumnValue(id)))
                .query();
    }

    public String findFirst(Class<?> clazz) {
        return selectFrom(clazz)
                .first()
                .query();
    }
}
