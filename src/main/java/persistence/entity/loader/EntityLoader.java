package persistence.entity.loader;

public interface EntityLoader {
    <T> T load(Class<T> clazz, String id);
}
