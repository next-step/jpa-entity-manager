package persistence.entity.entry;

public class EntityEntry {
    private EntityEntryStatus status;

    public EntityEntry(EntityEntryStatus status) {
        this.status = status;
    }

    public EntityEntryStatus getStatus() {
        return status;
    }

    public void setStatus(EntityEntryStatus status) {
        this.status = status;
    }
}
