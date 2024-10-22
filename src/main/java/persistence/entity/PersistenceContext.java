package persistence.entity;

import java.io.Serializable;
import java.util.List;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey, Object entity);

    void addEntity(EntityKey entityKey, Object entity);

    void addDatabaseSnapshot(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    boolean isManagedEntity(Object entity, Object id);
}
