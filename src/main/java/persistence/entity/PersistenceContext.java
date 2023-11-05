package persistence.entity;

import domain.Snapshot;

import java.util.Map;

public interface PersistenceContext {

    <T, I> T getEntity(Integer key, EntityPersister<T> persister, I input);

    Object addEntity(Integer key, Object id, Object entity);

    Object addEntity(Integer key, Snapshot snapshot);

    void removeEntity(Integer id);

    <T, I> Snapshot getDatabaseSnapshot(Integer key, EntityPersister<T> persister, I input);

    Map<Integer, Snapshot> comparison();

    void flush(Map<String, EntityPersister<?>> persister);
}
