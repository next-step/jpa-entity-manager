package persistence.sql.dml;

import persistence.dialect.Dialect;
import persistence.exception.FieldEmptyException;
import persistence.meta.EntityMeta;

public class DeleteQueryBuilder<T> extends DMLQueryBuilder<T> {
    public DeleteQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String getDeleteQuery(Object id) {
        if (id == null) {
            throw new FieldEmptyException("id가 비어 있으면 안 됩니다.");
        }

        return getDeleteQuery()
                + getFromTableQuery(entityMeta.getTableName())
                + whereId(getPkColumn(), id);
    }

    private String getDeleteQuery() {
        return dialect.deleteQuery();
    }
}
