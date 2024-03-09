package persistence.sql.dml;

import persistence.sql.QueryBuilder;
import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.ddl.domain.Table;
import persistence.sql.dml.domain.Value;
import persistence.sql.dml.domain.Values;

import java.util.stream.Collectors;

public class InsertQueryBuilder implements QueryBuilder {

    private static final String INSERT_QUERY = "INSERT INTO %s (%s) VALUES (%s);";

    private final Columns columns;
    private final Table table;
    private final Values values;

    public InsertQueryBuilder(Object entity) {
        Class<?> clazz = entity.getClass();
        this.table = new Table(clazz);
        this.columns = new Columns(clazz);
        this.values = new Values(columns, entity);
    }

    @Override
    public String build() {
        return String.format(
                INSERT_QUERY,
                table.getName(),
                generateColumns(),
                generateValues()
        );
    }

    private String generateColumns() {
        return columns.getColumns().stream()
                .filter(Column::isNotAutoIncrementId)
                .map(Column::getName)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.joining(COMMA));
    }

    private String generateValues() {
        return values.getValues().stream()
                .filter(Value::isNotAutoIncrementId)
                .map(Value::getValue)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.joining(COMMA));
    }
}
