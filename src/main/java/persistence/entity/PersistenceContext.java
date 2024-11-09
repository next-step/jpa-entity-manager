package persistence.entity;

import java.util.Optional;

public interface PersistenceContext {

    <T, ID> Optional<T> getEntity(ID id, Class<T> entityType);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    <ID> void addDatabaseSnapshot(ID id, Object snapshot);

    <T, ID> EntitySnapshot getDatabaseSnapshot(ID id, Class<T> entityType);

}
