package persistence.entity.persistencecontext;

public class DefaultEntityEntry implements EntityEntry {
    private EntityStatus previousStatus;
    private EntityStatus status;

    public DefaultEntityEntry(EntityStatus status) {
        this.status = status;
    }

    @Override
    public EntityStatus getStatus() {
        return status;
    }

    @Override
    public void updateStatus(EntityStatus status) {
        this.previousStatus = this.status;
        this.status = status;
    }
}
