package persistence.sql.dml;

import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.util.stream.Collectors;

public class UpdateQueryBuilder {

    public UpdateQueryBuilder() {
    }

    public String update(EntityTable entityTable, EntityColumns entityColumns, Object object, Object idValue) {
        String tableName = entityTable.getTableName();
        String idField = entityColumns.getIdFieldName();
        String formattedIdValue = getFormattedId(idValue);
        String setColumns = getSetColumns(entityColumns,object);

        return String.format("update %s set %s where %s = %s", tableName, setColumns, idField, formattedIdValue);
    }

    private String getSetColumns(EntityColumns entityColumns, Object object) {
        return entityColumns.getColumns().stream()
                .filter(entityColumn -> !entityColumn.isPrimaryKey())
                .filter(entityColumn -> !entityColumn.isTransient())
                .map(entityColumn -> {
                    String columnName = entityColumn.getColumnName();
                    String columnValue = entityColumn.getFieldValue(object);
                    return String.format("%s = %s", columnName, columnValue);
                })
                .collect(Collectors.joining(", "));
    }

    private String getFormattedId(Object idValue) {
        if (idValue instanceof String) {
            return String.format(("'%s'"), idValue);
        }
        return idValue.toString();
    }
}
