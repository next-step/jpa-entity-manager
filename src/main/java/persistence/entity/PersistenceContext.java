package persistence.entity;

public interface PersistenceContext {
	<T> T getEntity(Class<T> clazz, Long id);

	void addEntity(Long id, Object entity);

	void removeEntity(Long id, Object entity);
}
