package persistence.sql.dml.query.builder;

import static persistence.sql.query.QueryClauseGenerator.whereClause;

import java.util.List;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.metadata.TableName;

public class DeleteQueryBuilder {

    private static final String DELETE_FROM = "delete from";
    private final StringBuilder queryString;

    private DeleteQueryBuilder() {
        this.queryString = new StringBuilder();
    }

    public static DeleteQueryBuilder builder() {
        return new DeleteQueryBuilder();
    }

    public String build() {
        return queryString.toString();
    }

    public DeleteQueryBuilder delete(TableName tableName) {
        queryString.append( DELETE_FROM )
                .append( " " )
                .append( tableName.value() );
        return this;
    }

    public DeleteQueryBuilder where(List<WhereCondition> whereConditions) {
        queryString.append( whereClause(whereConditions) );
        return this;
    }

}
