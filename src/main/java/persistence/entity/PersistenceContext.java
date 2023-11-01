package persistence.entity;

public interface PersistenceContext {
	<T> T getEntity(Class<T> clazz, Long id);

	void addEntity(Long id, Object entity);

	Snapshot getDatabaseSnapshot(Object id, Object entity);

	Snapshot getCachedDatabaseSnapshot(Object id, Object entity);
}
