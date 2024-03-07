package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.UpdateColumnClause;
import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.UPDATE;

public class UpdateQueryBuilder {

    private static final UpdateQueryBuilder INSTANCE = new UpdateQueryBuilder();

    private UpdateQueryBuilder() {}

    public static UpdateQueryBuilder getInstance() {
        return INSTANCE;
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
