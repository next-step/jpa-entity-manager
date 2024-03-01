package persistence.entity.metadata;

public class EntityTable {

    private String entityName;
    private String tableName;

    public EntityTable(String entityName, String tableName) {
        this.entityName = entityName;
        this.tableName = tableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
