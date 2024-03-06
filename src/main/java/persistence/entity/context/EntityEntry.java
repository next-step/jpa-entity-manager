package persistence.entity.context;

public class EntityEntry {
    private Status status;

    private EntityEntry(Status status) {
        this.status = status;
    }

    public static EntityEntry create() {
        return new EntityEntry(Status.NONE);
    }

    public void managed() {
        this.status = Status.MANAGED;
    }

    public void deleted() {
        this.status = Status.DELETED;
    }

    public void gone() {
        this.status = Status.GONE;
    }

    public boolean isReadable() {
        return status.isReadable();
    }

    public boolean isAssignable() {
        return status.isAssignable();
    }

    public boolean isAlreadyRemoved() {
        return status.isAlreadyRemoved();
    }

    public boolean isRemovable() {
        return status.isRemovable();
    }
}
