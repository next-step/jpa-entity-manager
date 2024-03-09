package persistence.sql.entity;

import persistence.sql.entity.impl.EntityKey;

public interface PersistenceContext {
    Object getEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(EntityKey id);

//    Object getDatabaseSnapshot(Long id, Object entity);

    boolean isDirty(EntityKey id, Object entity);
}
