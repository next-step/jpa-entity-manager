package persistence.entity.persistencecontext;

public class EntityEntry {
    private Status status;

    public EntityEntry() {
        this.status = Status.MANAGED;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
