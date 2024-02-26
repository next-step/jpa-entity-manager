package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.dml.domain.Person;
import persistence.sql.meta.EntityColumns;
import persistence.sql.meta.EntityPrimaryKey;
import persistence.sql.meta.EntityTableMeta;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    private static final String KEY_VALUE_FORMAT = "%s=%s";
    private final EntityTableMeta entityTableMeta;
    private final EntityPrimaryKey entityPrimaryKey;
    private final EntityColumns entityColumns;
    private final Dialect dialect;

    public UpdateQueryBuilder(Class<Person> clazz, Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityPrimaryKey = EntityPrimaryKey.of(clazz);
        this.entityColumns = EntityColumns.of(clazz);
        this.dialect = dialect;
    }

    public String createUpdateQuery(Object object) {
        return String.format(dialect.getUpdateDefaultDmlQuery(), entityTableMeta.name(), setClause(object), whereClause(object));
    }

    private String setClause(Object object) {
        return this.entityColumns.getEntityColumns().stream()
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
