package persistence.context;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, String id);

    <T> void removeEntity(T instance);

    <T> T addEntity(T instance);

    <T> T getDatabaseSnapshot(T instance, String id);
}
