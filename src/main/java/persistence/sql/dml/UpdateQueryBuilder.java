package persistence.sql.dml;

import persistence.sql.meta.Table;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public static final String UPDATE_DEFAULT_DML = "update %s set %s where %s";
    private static final String KEY_VALUE_FORMAT = "%s=%s";

    public UpdateQueryBuilder() {
    }

    public String createUpdateQuery(Table table) {
        return String.format(UPDATE_DEFAULT_DML, table.name(), setClause(table), whereClause(table));
    }

    private String setClause(Table table) {
        return table.columns().getColumns().stream()
                .map(e -> setColumnSetting(e.getFieldName(), String.valueOf(e.value())))
                .collect(Collectors.joining(", "));
    }

    private String whereClause(Table table) {
        return setColumnSetting(table.primaryKey().name(), String.valueOf(table.primaryKey().value()));
    }

    private String setColumnSetting(String name, String value) {
        return String.format(KEY_VALUE_FORMAT, name, value);
    }
}
