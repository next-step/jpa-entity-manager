package persistence.entity;

public interface EntityEntry {
    Status getStatus();

    EntityPersister getEntityPersister();

    void setSaving();

    void setManaged();

    void setLoading();
}
