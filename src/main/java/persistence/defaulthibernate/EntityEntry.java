package persistence.defaulthibernate;

public class EntityEntry {
    EntryStatus status;

    public static EntityEntry status(EntryStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        return new EntityEntry(status);
    }

    EntryStatus status() {
        return status;
    }

    public void updateStatus(EntryStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
    }

    private EntityEntry(EntryStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        this.status = status;
    }

}
