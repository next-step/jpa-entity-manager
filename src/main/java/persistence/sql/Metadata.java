package persistence.sql;

import java.util.stream.Collectors;

public class Metadata {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public Metadata(Class<?> entityClass) {
        this(new EntityTable(entityClass), new EntityColumns(entityClass));
    }

    public Metadata(EntityTable entityTable, EntityColumns entityColumns) {
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public String getTableName() {
        return entityTable.getFullTableName();
    }

    public String getSchemaName() {
        return entityTable.getSchemaName();
    }

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public EntityColumns getEntityColumns() {
        return entityColumns;
    }

    public EntityColumnValues getEntityValuesFrom(Object entity) {
        return new EntityColumnValues(
            entityColumns.getColumns().stream()
                .map(entityColumn -> entityColumn.getEntityValueFrom(entity))
                .collect(Collectors.toList())
        );
    }
}
