package persistence.entity;

import domain.Snapshot;

import java.util.Map;

public interface PersistenceContext {

    <T, I> T getEntity(Integer hashCode, EntityPersister<T> persister, I input);

    Object addEntity(Integer hashCode, Object id, Object entity);

    Object addEntity(Integer hashCode, Snapshot snapshot);

    void removeEntity(Integer hashCode);

    <T, I> Snapshot getDatabaseSnapshot(Integer hashCode, EntityPersister<T> persister, I input);

    Map<Integer, Snapshot> comparison();

    void flush(Map<String, EntityPersister<?>> persister);
}
