package persistence.entity;

public class EntityEntry {

    EntityKey key;
    EntityStatus status;
    EntitySnapshot snapshot;
    Object entity;

    public static EntityEntry newLoadingEntity(EntityKey key) {
        return new EntityEntry(key, EntityStatus.LOADING, null, null);
    }

    public EntityEntry(EntityKey key, EntityStatus status, Object entity) {
        this(key, status, new EntitySnapshot(entity), entity);
    }

    public EntityEntry(EntityKey key, EntityStatus status, EntitySnapshot snapshot, Object entity) {
        this.key = key;
        this.status = status;
        this.snapshot = snapshot;
        this.entity = entity;
    }

    public Object entity() {
        return entity;
    }

    public boolean isDirty(Object entity) {
        return snapshot.hasDifferenceWith(entity);
    }

    public void updateEntity(Object entity) {
        this.entity = entity;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

    public void createSnapshot(Object entity) {
        this.snapshot = new EntitySnapshot(entity);
    }

    public void updateSnapshot(Object entity) {
        this.snapshot = new EntitySnapshot(entity);
    }

}
