package persistence.entity.context;

public class EntityEntry {
    public static final EntityEntry NONE = new EntityEntry(Status.NONE);

    private Status status;

    private EntityEntry(Status status) {
        this.status = status;
    }

    public static EntityEntry create() {
        return new EntityEntry(Status.INITIALIZING);
    }

    public Status getStatus() {
        return status;
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
}
