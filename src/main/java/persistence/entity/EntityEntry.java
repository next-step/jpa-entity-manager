package persistence.entity;

public class EntityEntry {

    private Status status;

    public EntityEntry(Status status) {
        this.status = status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
