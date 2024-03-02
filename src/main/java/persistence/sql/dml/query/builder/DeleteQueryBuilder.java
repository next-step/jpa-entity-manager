package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.DELETE;

public class DeleteQueryBuilder {

    private final TableName tableName;

    public DeleteQueryBuilder(final TableName tableName) {
        this.tableName = tableName;
    }

    public String toSql(final WhereClause whereClause) {
        return String.format(DELETE.getFormat(),
                tableName.getName(),
                whereClause.toSql()
        );
    }


}
