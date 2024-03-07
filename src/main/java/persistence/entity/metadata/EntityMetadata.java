package persistence.entity.metadata;

import java.util.HashMap;
import java.util.Map;

public class EntityMetadata {
    private EntityTable entityTable;
    private EntityColumns columns;

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(EntityTable entityTable) {
        this.entityTable = entityTable;
    }

    public void setColumns(EntityColumns columns) {
        this.columns = columns;
    }

    public String getTableName() {
        return entityTable.getTableName();
    }

    public EntityColumns getColumns() {
        return columns;
    }

    public EntityColumn getIdColumn() {
        return columns.getIdColumn();
    }

    public void setValue(Object object, String columnName, Object value) {
        columns.getColumnByColumnName(columnName).setValue(object, value);
    }

    private Object getValue(Object entity, String columnName) {
        return columns.getColumnByColumnName(columnName).getValue(entity);
    }

    public Map<String, Object> getColumnValues(Object entity) {
        Map<String, Object> columnValues = new HashMap<>();
        columns.getColumns()
                .forEach(column -> columnValues.put(column.getColumnName(), getValue(entity, column.getColumnName())));

        return columnValues;
    }

}
