package persistence.context;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, String id);

    <T> void removeEntity(T instance, String instanceId);

    <T> void addEntity(T inserted, String instanceId);
}
