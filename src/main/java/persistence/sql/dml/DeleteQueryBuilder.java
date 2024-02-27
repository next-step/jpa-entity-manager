package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimplePrimaryKey;
import persistence.sql.meta.simple.SimpleTableName;

public class DeleteQueryBuilder {
    private final SimpleTableName entityTableMeta;
    private final SimplePrimaryKey entityPrimaryKey;
    private final Dialect dialect;

    public DeleteQueryBuilder(Class<?> clazz, Dialect dialect) {
        this.entityTableMeta = SimpleTableName.of(clazz);
        this.entityPrimaryKey = SimplePrimaryKey.of(clazz);
        this.dialect = dialect;
    }

    // delete from %s where %s
    public String createDeleteQuery(Object object) {
        return String.format(dialect.getDeleteDefaultDmlQuery(), this.entityTableMeta.name(), deleteWhere(object));
    }

    private String deleteWhere(Object object) {
        return String.format("%s = %s", this.entityPrimaryKey.name(), this.entityPrimaryKey.value(object));
    }

}
