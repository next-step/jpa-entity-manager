package persistence.sql.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> clazz, Long id);

    void addEntity(Object entity);

    void removeEntity(Class<?> clazz, Long id);

    boolean containsEntity(Class<?> clazz, Long id);
}
