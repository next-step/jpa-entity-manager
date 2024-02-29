package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class InsertQueryBuilder {

    private static final String INSERT_DEFINITION = "INSERT INTO %s (%s) VALUES (%s)";

    private InsertQueryBuilder() {
    }

    private static class Holder {
        static final InsertQueryBuilder INSTANCE = new InsertQueryBuilder();
    }

    public static InsertQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table, Object object) {
        return String.format(INSERT_DEFINITION, table.getTableName(),
            columnsClause(table.getInsertColumns()), valueClause(table.getInsertColumns(), object));
    }

    private String columnsClause(List<Column> columns) {
        return columns.stream()
            .map(Column::getColumnName)
            .collect(Collectors.joining(COMMA.getValue()));
    }

    private String valueClause(List<Column> columns, Object object) {
        return columns.stream()
            .map(column -> column.getFieldValue(object))
            .map(String::valueOf)
            .collect(Collectors.joining(COMMA.getValue()));
    }
}
