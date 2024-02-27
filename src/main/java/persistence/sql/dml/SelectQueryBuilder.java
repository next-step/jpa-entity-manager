package persistence.sql.dml;

import java.util.List;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class SelectQueryBuilder {

    private static final String SELECT_DEFINITION = "SELECT %s FROM %s";

    private SelectQueryBuilder() {
    }

    private static class Holder {
        static final SelectQueryBuilder INSTANCE = new SelectQueryBuilder();
    }

    public static SelectQueryBuilder getInstance() {
        return Holder.INSTANCE;
    }

    public String generateQuery(Table table) {
        return String.format(SELECT_DEFINITION, columnsClause(table.getColumns()), table.getTableName());
    }

    private String columnsClause(List<Column> columns) {
        return columns.stream()
            .map(Column::getColumnName)
            .collect(Collectors.joining(COMMA.getValue()));
    }
}
