package persistence.entity;

public class EntityEntry {

    EntityKey key;
    EntityStatus status;
    EntitySnapshot snapshot;

    public EntityEntry(EntityKey key, EntityStatus status, EntitySnapshot snapshot) {
        this.key = key;
        this.status = status;
        this.snapshot = snapshot;
    }

}
