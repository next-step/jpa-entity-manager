package persistence.entity;

public interface EntityEntry {
    Status getStatus();

    EntityPersister getEntityPersister();

    void setSaving();

    void setManaged();

    void setLoading();

    void setDeleted();

    void setGone();

    void setReadOnly();

    boolean isReadOnly();
}
