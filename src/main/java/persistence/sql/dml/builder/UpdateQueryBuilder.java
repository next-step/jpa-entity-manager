package persistence.sql.dml.builder;

import persistence.sql.dml.column.DmlColumn;
import persistence.sql.dml.column.DmlColumns;
import persistence.sql.dml.statement.QueryStatement;

public class UpdateQueryBuilder {
    private static final String INSERT_QUERY_FORMAT = "update into %s (%s) values (%s)";
    public static final UpdateQueryBuilder INSTANCE = new UpdateQueryBuilder();

    public String update(Object entity) {
        DmlColumns dmlColumns = DmlColumns.of(entity);

        return QueryStatement.update(entity.getClass())
                .set(dmlColumns.getDmlColumns().toArray(DmlColumn[]::new))
                .where(DmlColumn.id(entity.getClass(), entity))
                .query();
    }
}
