package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.SELECT;

public class SelectQueryBuilder {
    private final TableName tableName;
    private final ColumnClause columnClause;

    public SelectQueryBuilder(final TableName tableName,
                              final ColumnClause columnClause) {
        this.tableName = tableName;
        this.columnClause = columnClause;
    }

    public String toSql(final WhereClause whereClause) {
        return String.format(SELECT.getFormat(),
                columnClause.toSql(),
                tableName.getName(),
                whereClause.toSql());
    }


}
