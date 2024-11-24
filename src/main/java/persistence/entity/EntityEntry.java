package persistence.entity;

public class EntityEntry {
    private EntityStatus status;
    private EntitySnapshot snapshot;
    private Object entity;

    private EntityEntry(EntityStatus status, EntitySnapshot snapshot, Object entity) {
        this.status = status;
        this.snapshot = snapshot;
        this.entity = entity;
    }

    public static EntityEntry of(EntityStatus status, Object entity) {
        return new EntityEntry(
                status,
                EntitySnapshot.of(entity),
                entity
        );
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void updateStatus(EntityStatus status) {
        this.status = status;
    }

    public EntitySnapshot getSnapshot() {
        return snapshot;
    }

    public Object getEntity() {
        return entity;
    }

    public Class<?> getEntityClass() {
        return entity.getClass();
    }

    public Long getId() {
        return EntityUtils.getIdValue(entity);
    }
}
