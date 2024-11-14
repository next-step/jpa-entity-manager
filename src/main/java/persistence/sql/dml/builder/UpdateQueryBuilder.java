package persistence.sql.dml.builder;

import persistence.metadata.ColumnName;
import persistence.metadata.FieldValue;
import persistence.metadata.TableName;
import persistence.metadata.WhereCondition;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.clause.QueryClause.whereClause;

public class UpdateQueryBuilder {
    private static final String UPDATE = "update";
    private static final String SET = "set";
    private static final String AND_COMMA = ", ";

    private final StringBuilder queryString;

    private UpdateQueryBuilder() {
        this.queryString = new StringBuilder();
    }

    public static UpdateQueryBuilder builder() {
        return new UpdateQueryBuilder();
    }

    public String build() {
        return queryString.toString();
    }

    public UpdateQueryBuilder update(TableName table) {
        queryString.append( UPDATE )
                .append( " " )
                .append( table.name() );
        return this;
    }

    public UpdateQueryBuilder set(List<FieldValue> fields) {
        queryString.append( " " )
                .append( SET )
                .append( " " )
                .append( setClause(fields) );
        return this;
    }

    public UpdateQueryBuilder where(List<WhereCondition> whereConditions) {
        queryString.append( whereClause(whereConditions) );
        return this;
    }

    private String setClause(List<FieldValue> fields) {
        return fields.stream()
                .map(field -> field.columnName().name() + " = " + field.fieldValueHandleToString())
                .collect(Collectors.joining(AND_COMMA));
    }
}
