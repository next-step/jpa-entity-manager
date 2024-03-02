package persistence.sql.dml;

import persistence.sql.mapping.Columns;
import persistence.sql.mapping.TableData;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    public static final String SET_COLUMN_FORMAT = "%s = %s";
    private final TableData table;
    private final Columns columns;

    public UpdateQueryBuilder(TableData table, Columns columns) {
        this.table = table;
        this.columns = columns;
    }

    public String build(Object entity, WhereBuilder whereBuilder) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("update ");
        stringBuilder.append(table.getName());
        stringBuilder.append(" set ");
        stringBuilder.append(valueClause(columns));

        if (whereBuilder.isEmpty()) {
            return stringBuilder.toString();
        }

        stringBuilder.append(whereBuilder.toClause());

        return stringBuilder.toString();
    }

    private String valueClause(Columns columns) {
        return columns.getValuesMap()
                .entrySet()
                .stream()
                .map(entry -> String.format(SET_COLUMN_FORMAT, entry.getKey(), ValueUtil.getValueString(entry.getValue())))
                .collect(Collectors.joining(", "));
    }
}
