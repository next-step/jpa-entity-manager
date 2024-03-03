package persistence.sql.dml;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.ddl.domain.Table;
import persistence.sql.dml.domain.Value;
import persistence.sql.dml.domain.Values;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder implements QueryBuilder {

    private static final String UPDATE_QUERY = "UPDATE %s SET %s%s;";

    private final Table table;
    private final Columns columns;
    private final Values values;
    private final WhereQueryBuilder whereQueryBuilder;

    public UpdateQueryBuilder(Object object) {
        Class<?> clazz = object.getClass();
        this.table = new Table(clazz);
        this.columns = new Columns(createColumns(clazz));
        this.values = new Values(createValues(object));
        this.whereQueryBuilder = new WhereQueryBuilder(values.getPrimaryKeyValue());
    }

    private List<Column> createColumns(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientAnnotationPresent)
                .map(Column::new)
                .collect(Collectors.toList());
    }

    private List<Value> createValues(Object object) {
        return columns.getColumns().stream()
                .map(column -> new Value(column, object))
                .filter(Value::isValueNotNull)
                .collect(Collectors.toList());
    }

    @Override
    public String build() {
        return String.format(
                UPDATE_QUERY,
                table.getName(),
                generateSetValues(),
                whereQueryBuilder.build()
        );
    }

    private String generateSetValues() {
        return values.getValues().stream()
                .filter(Value::isNotPrimaryKeyValue)
                .map(x -> x.getColumn().getName() + " = " + x.getValue())
                .collect(Collectors.joining(COMMA));
    }

}
