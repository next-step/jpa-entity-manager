package persistence.sql.dml.query.builder;

import persistence.sql.dml.query.clause.WhereClause;
import persistence.sql.entity.model.TableName;

import static persistence.sql.constant.SqlFormat.DELETE;

public class DeleteQueryBuilder {

    private static DeleteQueryBuilder instance;

    private DeleteQueryBuilder() {}
    public static DeleteQueryBuilder getInstance() {
        if(instance == null) {
            instance = new DeleteQueryBuilder();
        }
        return instance;
    }

    public String toSql(final TableName tableName,
                        final WhereClause whereClause) {
        return String.format(DELETE.getFormat(),
                tableName.getName(),
                whereClause.toSql()
        );
    }


}
