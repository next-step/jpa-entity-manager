package persistence.sql.dml.query.builder;

import static persistence.sql.query.QueryClauseGenerator.whereClause;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.query.ColumnNameValue;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.metadata.TableName;

public class UpdateQueryBuilder {

    private static final String UPDATE = "update";
    private static final String SET = "set";

    private final Dialect dialect;
    private final StringBuilder queryString;

    private UpdateQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
        this.queryString = new StringBuilder();
    }

    public static UpdateQueryBuilder builder(Dialect dialect) {
        return new UpdateQueryBuilder(dialect);
    }

    public String build() {
        return queryString.toString();
    }

    public UpdateQueryBuilder update(TableName tableName) {
        queryString.append( UPDATE )
                .append( " " )
                .append( tableName.value() );
        return this;
    }

    public UpdateQueryBuilder set(List<ColumnNameValue> columns) {
        queryString.append( " " )
                .append( SET )
                .append( " " )
                .append( setClause(columns) );
        return this;
    }

    public UpdateQueryBuilder where(List<WhereCondition> whereConditions) {
        queryString.append( whereClause(whereConditions) );
        return this;
    }

    private String setClause(List<ColumnNameValue> columns) {
        return columns.stream()
                .map(column -> column.columnName().value() + " = " + column.columnValueString())
                .collect(Collectors.joining(", "));
    }

}
