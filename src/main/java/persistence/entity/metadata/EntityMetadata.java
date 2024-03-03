package persistence.entity.metadata;

public class EntityMetadata {

    private EntityTable entityTable;
    private EntityColumns columns;

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(EntityTable entityTable) {
        this.entityTable = entityTable;
    }

    public EntityColumns getColumns() {
        return columns;
    }

    public void setColumns(EntityColumns columns) {
        this.columns = columns;
    }

}
