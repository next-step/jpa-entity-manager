package persistence.sql.dml;

import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.util.stream.Collectors;

public class SelectQueryBuilder {

    public String findAll(EntityTable entityTable, EntityColumns entityColumns) {
        String tableName = entityTable.getTableName();
        String tableColumns = getTableColumns(entityColumns);
        return String.format("select %s FROM %s", tableColumns, tableName);
    }

    public String findById(EntityTable entityTable, EntityColumns entityColumns, Object idValue) {
        String selectQuery = findAll(entityTable, entityColumns);
        String idField = entityColumns.getIdFieldName();
        String formattedIdValue = getFormattedId(idValue);
        return String.format("%s where %s = %s", selectQuery, idField, formattedIdValue);
    }

    private String getTableColumns(EntityColumns entityColumns) {
        return entityColumns.getColumns()
                .stream()
                .filter(entityColumn -> !entityColumn.isTransient())
                .map(EntityColumn::getColumnName)
                .collect(Collectors.joining(", "));
    }

    private String getFormattedId(Object idValue) {
        if (idValue instanceof String) {
            return String.format(("'%s'"), idValue);
        }
        return idValue.toString();
    }
}
