package persistence.entity.context;

public interface PersistenceContext {

    <T> T getEntity(final Object key, final String className);

    void addEntity(final Object key, final Object entity);

    void removeEntity(final Object key, final Object entity);
}
