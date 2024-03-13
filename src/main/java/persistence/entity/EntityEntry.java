package persistence.entity;

public interface EntityEntry {

    void preFind();

    void postFind();

    void prePersist();

    void postPersist();

    void preUpdate();

    void postUpdate();

    void preRemove();

    void postRemove();

    void preReadOnly();
    void postReadOnly();
}
