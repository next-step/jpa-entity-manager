package persistence.sql.dml;

import persistence.sql.entity.EntityColumn;
import persistence.sql.entity.EntityColumns;
import persistence.sql.entity.EntityTable;

import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;


    public SelectQueryBuilder(EntityTable entityTable, EntityColumns entityColumns) {
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public String findAll() {
        String tableName = entityTable.getTableName();
        String tableColumns = getTableColumns();
        return String.format("select %s FROM %s", tableColumns, tableName);
    }

    public String findById(Object idValue) {
        String selectQuery = findAll();
        String idField = entityColumns.getIdFieldName();
        String formattedIdValue = getFormattedId(idValue);
        return String.format("%s where %s = %s", selectQuery, idField, formattedIdValue);
    }

    private String getTableColumns() {
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
