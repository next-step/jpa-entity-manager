package persistence.entity;

public class SimpleEntityEntry implements EntityEntry {

    private final Object entity;
    private Status status;

    public SimpleEntityEntry(Object entity, Status status) {
        this.entity = entity;
        this.status = status;
    }

    @Override
    public void updateStatus(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

}
