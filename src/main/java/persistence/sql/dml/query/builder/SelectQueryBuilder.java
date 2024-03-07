package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.ColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.SELECT;

public class SelectQueryBuilder {

    private static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();

    private SelectQueryBuilder() { }

    public static SelectQueryBuilder getInstance() {
        return INSTANCE;
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
