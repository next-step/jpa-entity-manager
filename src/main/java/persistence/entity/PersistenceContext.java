package persistence.entity;

public interface PersistenceContext {
	<T> T getEntity(Class<T> clazz, Object id);

	void addEntity(Object id, Object entity);

	void removeEntity(Object id, Object entity);

	Snapshot getDatabaseSnapshot(Object id, Object entity);

	Snapshot getCachedDatabaseSnapshot(Object id, Object entity);
}
