package persistence.entity;

public class EntityEntryImpl implements EntityEntry {
    private Status status;
    final private EntityPersister entityPersister;
    final private EntityLoader entityLoader;


    public EntityEntryImpl(EntityPersister entityPersister, EntityLoader entityLoader, Status status) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public EntityPersister getEntityPersister() {
        return entityPersister;
    }

    @Override
    public void setSaving() {
        status = Status.SAVING;
    }

    @Override
    public void setManaged() {
        status = Status.MANAGED;
    }
}
