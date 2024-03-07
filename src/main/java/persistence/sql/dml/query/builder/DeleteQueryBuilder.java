package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.DELETE;

public class DeleteQueryBuilder {

    private static final DeleteQueryBuilder INSTANCE = new DeleteQueryBuilder();

    private DeleteQueryBuilder() {}
    public static DeleteQueryBuilder getInstance() {
        return INSTANCE;
    }

    public String toSql(final TableName tableName,
                        final WhereClause whereClause) {
        return String.format(DELETE.getFormat(),
                tableName.getName(),
                whereClause.toSql()
        );
    }


}
