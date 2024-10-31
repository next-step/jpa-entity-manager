package persistence.entity;

public interface EntityLoader {
    <T> T find(Class<T> clazz, Object id);

    <T> boolean exists(Class<T> clazz, Object id);
}
