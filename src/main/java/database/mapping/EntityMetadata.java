package database.mapping;

import database.dialect.Dialect;
import database.mapping.column.EntityColumn;

import java.lang.reflect.Field;
import java.util.List;

public class EntityMetadata {
    private final TableMetadata tableMetadata;
    private final ColumnsMetadata columnsMetadata;

    private EntityMetadata(TableMetadata tableMetadata, ColumnsMetadata columnsMetadata) {
        this.tableMetadata = tableMetadata;
        this.columnsMetadata = columnsMetadata;
    }

    public static EntityMetadata fromClass(Class<?> clazz) {
        return new EntityMetadata(
                new TableMetadata(clazz),
                ColumnsMetadata.fromClass(clazz)
        );
    }

    public String getTableName() {
        return tableMetadata.getTableName();
    }

    public String getEntityClassName() {
        return tableMetadata.getEntityClassName();
    }

    public List<String> getAllColumnNames() {
        return columnsMetadata.getAllColumnNames();
    }

    public String getJoinedAllColumnNames() {
        return String.join(", ", columnsMetadata.getAllColumnNames());
    }

    public List<String> getColumnDefinitions(Dialect dialect) {
        return columnsMetadata.getColumnDefinitions(dialect);
    }

    public String getPrimaryKeyColumnName() {
        return columnsMetadata.getPrimaryKeyColumnName();
    }

    public List<String> getGeneralColumnNames() {
        return columnsMetadata.getGeneralColumnNames();
    }

    public List<EntityColumn> getGeneralColumns() {
        return columnsMetadata.getGeneralColumns();
    }

    public Long getPrimaryKeyValue(Object entity) {
        return columnsMetadata.getPrimaryKeyValue(entity);
    }

    public Field getFieldByColumnName(String columnName) {
        return columnsMetadata.getFieldByColumnName(columnName);
    }

    public boolean requiresIdWhenInserting() {
        return columnsMetadata.isRequiredId();
    }
}
