package persistence.entity;

public class EntityEntry {
    private EntityStatus status;

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }
}
