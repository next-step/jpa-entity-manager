package persistence.sql.dml.statement;

import jakarta.persistence.Transient;
import persistence.sql.base.ColumnNames;
import persistence.sql.base.TableName;
import persistence.sql.dml.column.DmlColumn;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryStatement {
    private final StringBuilder queryBuilder;

    public QueryStatement(StringBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public static QueryStatement selectFrom(Class<?> clazz) {
        String tableName = new TableName(clazz).getName();
        String selectColumns = getSelectColumnNames(clazz).names();

        StringBuilder stringBuilder = new StringBuilder(String.format("select %s from %s", selectColumns, tableName));
        return new QueryStatement(stringBuilder);
    }

    private static ColumnNames getSelectColumnNames(Class<?> clazz) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .collect(Collectors.toList());

        return ColumnNames.of(fields);
    }

    public static QueryStatement delete(Class<?> clazz) {
        String tableName = new TableName(clazz).getName();
        StringBuilder stringBuilder = new StringBuilder(String.format("delete from %s", tableName));
        return new QueryStatement(stringBuilder);
    }

    public static QueryStatement update(Class<?> clazz) {
        String tableName = new TableName(clazz).getName();
        StringBuilder stringBuilder = new StringBuilder(String.format("update %s", tableName));
        return new QueryStatement(stringBuilder);
    }

    public QueryStatement set(DmlColumn... dmlColumns) {
        String setValues = Arrays.stream(dmlColumns)
                .map(dmlColumn -> String.format("%s = %s", dmlColumn.name(), dmlColumn.value()))
                .collect(Collectors.joining(", "));

        queryBuilder.append(String.format(" set %s", setValues));
        return this;
    }

    public QueryStatement where(DmlColumn dmlColumn) {
        queryBuilder.append(" where ");
        queryBuilder.append(String.format("%s=%s", dmlColumn.name(), dmlColumn.value()));
        return this;
    }

    public QueryStatement first() {
        queryBuilder.append(" order by id desc limit 1");
        return this;
    }

    public String query() {
        return queryBuilder.toString();
    }
}
