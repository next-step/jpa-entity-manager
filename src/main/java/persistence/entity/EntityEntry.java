package persistence.entity;

public class EntityEntry {
    private Status status;
    final private EntityPersister entityPersister;
    final private EntityLoader entityLoader;

    public EntityEntry(EntityPersister entityPersister, EntityLoader entityLoader, Status status) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
