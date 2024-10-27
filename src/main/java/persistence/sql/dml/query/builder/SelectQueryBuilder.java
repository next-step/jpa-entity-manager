package persistence.sql.dml.query.builder;

import static persistence.sql.query.QueryClauseGenerator.columnClause;
import static persistence.sql.query.QueryClauseGenerator.whereClause;

import java.util.List;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.metadata.ColumnName;
import persistence.sql.metadata.TableName;

public class SelectQueryBuilder {

    private static final String SELECT = "select";
    private static final String FROM = "from";

    private final StringBuilder queryString;

    private SelectQueryBuilder() {
        this.queryString = new StringBuilder();
    }

    public static SelectQueryBuilder builder() {
        return new SelectQueryBuilder();
    }

    public String build() {
        return queryString.toString();
    }

    public SelectQueryBuilder select(List<ColumnName> columnNames) {
        queryString.append( SELECT )
                .append( " " )
                .append( columnClause(columnNames) );
        return this;
    }

    public SelectQueryBuilder from(TableName tableName) {
        queryString.append( " " )
                .append( FROM )
                .append( " " )
                .append( tableName.value() );
        return this;
    }

    public SelectQueryBuilder where(List<WhereCondition> whereConditions) {
        if (whereConditions.isEmpty()) {
            return this;
        }
        queryString.append( whereClause(whereConditions) );
        return this;
    }

}
