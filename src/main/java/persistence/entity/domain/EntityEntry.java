package persistence.entity.domain;

public class EntityEntry {

    private EntityStatus status;

    public EntityEntry(EntityStatus status) {
        this.status = status;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

}
