package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.ValueClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.INSERT;

public class InsertQueryBuilder {
    private final TableName tableName;

    public InsertQueryBuilder(final TableName tableName) {
        this.tableName = tableName;
    }

    public String toSql(final ColumnClause columnClause,
                        final ValueClause valueClause) {

        return String.format(INSERT.getFormat(),
                tableName.getName(),
                columnClause.toSql(),
                valueClause.toSql());
    }
}
