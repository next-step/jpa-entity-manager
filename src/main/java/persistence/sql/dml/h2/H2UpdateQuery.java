package persistence.sql.dml.h2;

import persistence.sql.util.ColumnConditions;
import persistence.sql.util.TableName;

public final class H2UpdateQuery {
    private H2UpdateQuery() {}

    public static String build(Object entity) {
        return new StringBuilder()
                .append("UPDATE ")
                .append(TableName.build(entity.getClass()))
                .append(" SET ")
                .append(ColumnConditions.forUpsert(entity))
                .append(H2WhereIdQuery.build(entity))
                .toString();
    }
}
