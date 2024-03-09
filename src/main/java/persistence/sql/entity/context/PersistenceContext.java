package persistence.sql.entity.context;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity, Object id);

    void removeEntity(Object entity);

    void removeAll();

    <T> T getDatabaseSnapshot(Class<?> clazz, Object id);
}
