package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.QueryBuilder;
import persistence.sql.constant.SqlConstant;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class UpdateQueryBuilder implements QueryBuilder {

    private static final String UPDATE_TABLE_DEFINITION = "UPDATE %s SET %s";

    private UpdateQueryBuilder() {
    }

    public static UpdateQueryBuilder from() {
        return new UpdateQueryBuilder();
    }

    @Override
    public String generateQuery(Object object) {
        Table table = Table.from(object.getClass());
        return String.format(UPDATE_TABLE_DEFINITION, table.getTableName(), valueClause(table.getColumns(), object));
    }

    private String valueClause(List<Column> columns, Object object) {
        return columns.stream()
            .filter(Column::isUpdatable)
            .map(column -> column.getColumnName() + SqlConstant.EQUALS.getValue() + column.getFieldValue(object))
            .collect(Collectors.joining(COMMA.getValue()));
    }
}
