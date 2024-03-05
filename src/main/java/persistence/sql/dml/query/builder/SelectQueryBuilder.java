package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.SELECT;

public class SelectQueryBuilder {

    private SelectQueryBuilder() { }

    private static class SelectQuerySingleton {
        private static final SelectQueryBuilder SELECT_QUERY_BUILDER = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return SelectQuerySingleton.SELECT_QUERY_BUILDER;
    }

    public String toSql(final TableName tableName,
                        final ColumnClause columnClause,
                        final WhereClause whereClause) {
        return String.format(SELECT.getFormat(),
                columnClause.toSql(),
                tableName.getName(),
                whereClause.toSql());
    }


}
