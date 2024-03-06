package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.ValueClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.INSERT;

public class InsertQueryBuilder {

    private InsertQueryBuilder() {
    }

    private static class InsertQuerySingleton {
        private static final InsertQueryBuilder INSERT_QUERY_BUILDER = new InsertQueryBuilder();
    }

    public static InsertQueryBuilder getInstance() {
        return InsertQuerySingleton.INSERT_QUERY_BUILDER;
    }

    public String toSql(final TableName tableName,
                        final ColumnClause columnClause,
                        final ValueClause valueClause) {

        return String.format(INSERT.getFormat(),
                tableName.getName(),
                columnClause.toSql(),
                valueClause.toSql());
    }
}
