package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

public class EntityKey {
    private final EntityMetadata entityMetadata;
    private final Object id;

    private EntityKey(Class<?> clazz, Object id) {
        this.entityMetadata = EntityMetadata.from(clazz);
        this.id = id;
    }

    public static EntityKey of(Class<?> clazz, Object id) {
        return new EntityKey(clazz, id);
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
