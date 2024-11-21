package persistence.entity;

public class EntityEntry {
    private Status status;

    private EntityEntry(Status status) {
        this.status = status;
    }

    public static EntityEntry createManaged() {
        return new EntityEntry(Status.MANAGED);
    }

    public void setDeleted() {
        this.status = Status.DELETED;
    }
}
