package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.DELETE;

public class DeleteQueryBuilder {

    private DeleteQueryBuilder() {}

    private static class DeleteQuerySingleton {
        private static final DeleteQueryBuilder DELETE_QUERY_BUILDER = new DeleteQueryBuilder();
    }

    public static DeleteQueryBuilder getInstance() {
        return DeleteQuerySingleton.DELETE_QUERY_BUILDER;
    }

    public String toSql(final TableName tableName,
                        final WhereClause whereClause) {
        return String.format(DELETE.getFormat(),
                tableName.getName(),
                whereClause.toSql()
        );
    }


}
