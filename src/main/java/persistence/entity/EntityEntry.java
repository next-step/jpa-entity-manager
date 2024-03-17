package persistence.entity;

public class EntityEntry {

    private final Object entity;
    private EntityStatus status;

    private EntityEntry(Object entity, EntityStatus status) {
        this.entity = entity;
        this.status = status;
    }

    public static EntityEntry from(Object entity) {
        return new EntityEntry(entity, EntityStatus.LOADING);
    }
}
