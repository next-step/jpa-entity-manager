package persistence.entity;

import java.util.List;

public interface PersistenceContext {

    Object getEntity(EntityKey entityKey);

    EntitySnapshot getDatabaseSnapshot(EntityKey entityKey, Object entity);

    void addEntity(EntityKey entityKey, Object entity);

    void addDatabaseSnapshot(EntityKey entityKey, Object entity);

    List<String> dirtyCheck(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);
}
