package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public static final String UPDATE_DEFAULT_DML = "update %s set %s where %s";
    private static final String KEY_VALUE_FORMAT = "%s=%s";
    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;

    public UpdateQueryBuilder(String tableName, PrimaryKey primaryKey, Columns columns) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
    }

    public String createUpdateQuery(Object object) {
        return String.format(UPDATE_DEFAULT_DML, tableName, setClause(object), whereClause(object));
    }

    private String setClause(Object object) {
        return this.columns.getColumns().stream()
                .map(e -> setColumnSetting(e.getFieldName(), e.value(object)))
                .collect(Collectors.joining(", "));
    }

    private String whereClause(Object object) {
        return setColumnSetting(this.primaryKey.name(), this.primaryKey.value(object));
    }

    private String setColumnSetting(String name, String value) {
        return String.format(KEY_VALUE_FORMAT, name, value);
    }
}
