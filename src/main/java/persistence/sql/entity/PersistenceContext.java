package persistence.sql.entity;

import persistence.sql.meta.PrimaryKey;

public interface PersistenceContext {
    Object getEntity(PrimaryKey id);

    void addEntity(PrimaryKey id, Object entity);

    void removeEntity(PrimaryKey id);

//    Object getDatabaseSnapshot(Long id, Object entity);

    boolean isDirty(PrimaryKey id, Object entity);
}
