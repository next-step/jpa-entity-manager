package persistence.sql.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> clazz, Long id);

    void addEntity(Object entity, Long id);

    void removeEntity(Class<?> clazz, Long id);

    boolean containsEntity(Class<?> clazz, Long id);

    Object getDatabaseSnapshot(Long id, Object entity);
}
