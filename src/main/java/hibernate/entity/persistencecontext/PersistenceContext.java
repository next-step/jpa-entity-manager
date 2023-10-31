package hibernate.entity.persistencecontext;

import hibernate.EntityEntry;
import hibernate.Status;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(Object id, Object entity);

    void addEntity(Object id, Object entity, Status status);

    void addEntityEntry(Object entity, Status status);

    void removeEntity(Object entity);

    EntitySnapshot getDatabaseSnapshot(EntityKey id);
}
