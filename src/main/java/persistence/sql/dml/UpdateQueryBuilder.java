package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.Table;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public static final String UPDATE_DEFAULT_DML = "update %s set %s where %s";
    private static final String KEY_VALUE_FORMAT = "%s=%s";

    public UpdateQueryBuilder() {
    }

    public String createUpdateQuery(Object object) {
        Table table = Table.ofInstance(object);

        return String.format(UPDATE_DEFAULT_DML, table.name(), setClause(table, object), whereClause(table, object));
    }

    private String setClause(Table table, Object object) {
        return table.columns().getColumns().stream()
                .map(e -> setColumnSetting(e.getFieldName(), e.value(object)))
                .collect(Collectors.joining(", "));
    }

    private String whereClause(Table table, Object object) {
        return setColumnSetting(table.primaryKey().name(), table.primaryKey().value(object));
    }

    private String setColumnSetting(String name, String value) {
        return String.format(KEY_VALUE_FORMAT, name, value);
    }
}
