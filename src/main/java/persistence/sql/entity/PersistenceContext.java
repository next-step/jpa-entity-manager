package persistence.sql.entity;

import persistence.sql.entity.impl.EntityEntry;
import persistence.sql.entity.impl.EntityKey;

public interface PersistenceContext {
    Object getEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(EntityKey id);

    boolean isDirty(EntityKey id, Object entity);

    EntityEntry getEntityEntry(EntityKey id);
}
