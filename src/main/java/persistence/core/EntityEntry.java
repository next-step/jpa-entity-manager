package persistence.core;

public class EntityEntry {
    private Status status;

    public EntityEntry(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
