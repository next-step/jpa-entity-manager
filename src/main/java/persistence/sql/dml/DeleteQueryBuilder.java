package persistence.sql.dml;

import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

public class DeleteQueryBuilder {

    public String delete(EntityTable entityTable, EntityColumns entityColumns, Object idValue) {
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
