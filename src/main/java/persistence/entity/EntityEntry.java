package persistence.entity;

public class EntityEntry {

    private final Object entity;
    private EntityStatus entityStatus;

    private EntityEntry(Object entity, EntityStatus entityStatus) {
        this.entity = entity;
        this.entityStatus = entityStatus;
    }

    public static EntityEntry from(Object entity) {
        return new EntityEntry(entity, EntityStatus.LOADING);
    }

}
