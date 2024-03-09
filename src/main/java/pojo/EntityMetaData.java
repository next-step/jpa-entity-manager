package pojo;

import jakarta.persistence.Entity;

public class EntityMetaData {

    private final TableInfo tableInfo;

    public EntityMetaData(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalStateException("Entity 클래스가 아닙니다.");
        }
        this.tableInfo = new TableInfo(clazz);
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }
}
