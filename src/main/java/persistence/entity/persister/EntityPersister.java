package persistence.entity.persister;

public interface EntityPersister {
    <T> T load(Class<T> clazz, String id);

    <T> T update(T old, T updated);

    <T> T insert(T instance);

    <T> void remove(T entity, String id);
}
