package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Long entityId);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Long id, Object entity);

}
