package persistence.entity;

public class EntityEntry {

    EntityKey key;
    EntityStatus status;

    public EntityEntry(EntityKey key, EntityStatus status) {
        this.key = key;
        this.status = status;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

}
