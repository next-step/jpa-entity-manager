package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.ValueClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.INSERT;

public class InsertQueryBuilder {

    private static InsertQueryBuilder instance;

    private InsertQueryBuilder() {
    }

    public static InsertQueryBuilder getInstance() {
        if(instance == null) {
            instance = new InsertQueryBuilder();
        }
        return instance;
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
