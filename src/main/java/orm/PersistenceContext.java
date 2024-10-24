package orm;

public interface PersistenceContext {

    <T> T getEntity(Class<T> entityClazz, Object id);

    <T> T addEntity(T entity);

    boolean containsEntity(EntityKey key);

    void updateEntity(Object entity);

    void removeEntity(Object entity);

    void clear();
}
