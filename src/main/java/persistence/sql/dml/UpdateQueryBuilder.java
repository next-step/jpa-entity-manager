package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.TableName;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private static final String KEY_VALUE_FORMAT = "%s=%s";
    private final TableName tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final Dialect dialect;

    public UpdateQueryBuilder(EntityMetaCreator entityMetaCreator, Dialect dialect) {
        this.tableName = entityMetaCreator.createTableName();
        this.primaryKey = entityMetaCreator.createPrimaryKey();
        this.columns = entityMetaCreator.createColumns();
        this.dialect = dialect;
    }

    public String createUpdateQuery(Object object) {
        return String.format(dialect.getUpdateDefaultDmlQuery(), tableName.name(), setClause(object), whereClause(object));
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
