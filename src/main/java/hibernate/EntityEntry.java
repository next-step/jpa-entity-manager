package hibernate;

public class EntityEntry {

    private Status status;

    public EntityEntry(Status status) {
        this.status = status;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}
