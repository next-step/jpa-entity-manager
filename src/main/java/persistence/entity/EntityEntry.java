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

    public Object getEntity() {
        return entity;
    }

    public boolean isReadOnly() {
        return this.entityStatus == EntityStatus.READ_ONLY;
    }

    public void manage() {
        this.entityStatus = EntityStatus.MANAGED;
    }

    public void save() {
        this.entityStatus = EntityStatus.SAVING;
    }

    public void delete() {
        this.entityStatus = EntityStatus.DELETED;
    }


    public void gone() {
        this.entityStatus = EntityStatus.GONE;
    }
}
