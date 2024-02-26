package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityPrimaryKey;
import persistence.sql.meta.EntityTableMeta;

public class DeleteQueryBuilder {
    private final EntityTableMeta entityTableMeta;
    private final EntityPrimaryKey entityPrimaryKey;
    private final Dialect dialect;

    public DeleteQueryBuilder(Class<?> clazz, Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityPrimaryKey = EntityPrimaryKey.of(clazz);
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
