package persistence.entity;

public class EntityEntry {

    private EntityStatus status;

    private EntityEntry(EntityStatus status) {
        this.status = status;
    }

    public static EntityEntry loading() {
        return new EntityEntry(EntityStatus.LOADING);
    }

    public void save() {
        this.status = EntityStatus.SAVING;
    }

    public void delete() {
        this.status = EntityStatus.DELETED;
    }

    public void gone() {
        this.status = EntityStatus.GONE;
    }

    public void readOnly() {
        this.status = EntityStatus.READ_ONLY;
    }

    public void managed() {
        this.status = EntityStatus.MANAGED;
    }

    public boolean isGone() {
        return status == EntityStatus.GONE;
    }

    public boolean isReadOnly() {
        return status == EntityStatus.READ_ONLY;
    }

    public EntityStatus getStatus() {
        return status;
    }
}
