package persistence.entity;

public interface EntityEntry {

    void prePersist();

    void postPersist();

    void preRemove();

    void postRemove();

    void preUpdate();

    void postUpdate();

    void preLoad();

    void postLoad();

}
