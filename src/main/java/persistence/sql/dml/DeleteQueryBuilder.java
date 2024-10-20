package persistence.sql.dml;

import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

public class DeleteQueryBuilder {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public DeleteQueryBuilder(EntityTable entityTable, EntityColumns entityColumns) {
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public String delete(Object idValue) {
        String tableName = entityTable.getTableName();
        String idField = entityColumns.getIdFieldName();
        String formattedIdValue = getFormattedId(idValue);
        return String.format("delete FROM %s where %s = %s", tableName, idField, formattedIdValue);
    }

    private String getFormattedId(Object idValue) {
        if (idValue instanceof String) {
            return String.format(("'%s'"), idValue);
        }
        return idValue.toString();
    }
}
