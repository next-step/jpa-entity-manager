package persistence.entity;

public class EntityEntry {
    EntryStatus status;

    public EntityEntry(EntryStatus status) {
        this.status = status;
    }
    EntryStatus updateStatus(EntryStatus status) {
        this.status = status;
        return status;
    }
    EntryStatus getStatus() {
        return status;
    }
}
