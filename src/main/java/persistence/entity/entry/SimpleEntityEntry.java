package persistence.entity.entry;

public class SimpleEntityEntry implements EntityEntry {
    private Status status;

    public SimpleEntityEntry(Status status) {
        this.status = status;
    }

    @Override
    public void updateStatus(Status toStatus) {
        this.status = toStatus;
    }
}
