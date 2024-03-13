package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

public class EntityId {
    private final EntityMetadata entityMetadata;
    private final Object id;

    private EntityId(Class<?> clazz, Object id) {
        this.entityMetadata = EntityMetadata.of(clazz);
        this.id = id;
    }

    public static EntityId of(Class<?> clazz, Object id) {
        return new EntityId(clazz, id);
    }

    public Class<?> getClazz() {
        return entityMetadata.getClazz();
    }

    public Object getName() {
        return entityMetadata.getPrimaryKey().getName();
    }

    public Object getValue() {
        return id;
    }
}
