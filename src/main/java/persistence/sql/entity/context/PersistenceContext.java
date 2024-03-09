package persistence.sql.entity.context;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity, Object id);

    void removeEntity(Object entity);

    void goneEntity(Object entity);

    void saving(Object entity);

    void loading(Object entity, Object id);

    void readOnly(Object entity, Object id);

    void removeAll();

    <T> T getDatabaseSnapshot(Class<?> clazz, Object id);

    boolean isGone(Class<?> clazz, Object id);

    boolean isReadOnly(Object entity);
}
