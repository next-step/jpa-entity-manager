package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.TableName;
import persistence.sql.meta.simple.SimpleColumns;
import persistence.sql.meta.simple.SimplePrimaryKey;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private static final String KEY_VALUE_FORMAT = "%s=%s";
    private final TableName tableName;
    private final SimplePrimaryKey entityPrimaryKey;
    private final SimpleColumns entityColumns;
    private final Dialect dialect;

    public UpdateQueryBuilder(TableName tableName, Class<?> clazz, Dialect dialect) {
        this.tableName = tableName;
        this.entityPrimaryKey = SimplePrimaryKey.of(clazz);
        this.entityColumns = SimpleColumns.of(clazz);
        this.dialect = dialect;
    }

    public String createUpdateQuery(Object object) {
        return String.format(dialect.getUpdateDefaultDmlQuery(), tableName.name(), setClause(object), whereClause(object));
    }

    private String setClause(Object object) {
        return this.entityColumns.getColumns().stream()
                .map(e -> setColumnSetting(e.getFieldName(), e.value(object)))
                .collect(Collectors.joining(", "));
    }

    private String whereClause(Object object) {
        return setColumnSetting(this.entityPrimaryKey.name(), this.entityPrimaryKey.value(object));
    }

    private String setColumnSetting(String name, String value) {
        return String.format(KEY_VALUE_FORMAT, name, value);
    }
}
