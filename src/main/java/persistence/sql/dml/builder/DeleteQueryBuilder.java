package persistence.sql.dml.builder;

import persistence.dialect.Dialect;
import persistence.metadata.TableName;
import persistence.metadata.WhereCondition;

import java.util.List;

import static persistence.clause.QueryClause.whereClause;

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

    public DeleteQueryBuilder delete(TableName table) {
        queryString.append( DELETE_FROM )
                .append( " " )
                .append( table.name() );
        return this;
    }

    public DeleteQueryBuilder where(List<WhereCondition> whereConditions) {
        queryString.append( whereClause(whereConditions) );
        return this;
    }
}
