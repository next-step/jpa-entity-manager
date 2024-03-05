package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.UpdateColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.UPDATE;

public class UpdateQueryBuilder {

    private UpdateQueryBuilder() {}

    private static class UpdateQuerySingleton {
        private static final UpdateQueryBuilder UPDATE_QUERY_BUILDER = new UpdateQueryBuilder();
    }

    public static UpdateQueryBuilder getInstance() {
        return UpdateQuerySingleton.UPDATE_QUERY_BUILDER;
    }

    public String toSql(final TableName tableName,
                        final UpdateColumnClause updateColumnClause,
                        final WhereClause whereClause) {
        return String.format(UPDATE.getFormat(),
                tableName.getName(),
                updateColumnClause.toSql(),
                whereClause.toSql());
    }
}
