package persistence.entity.loader;

public interface EntityLoader {
    public <T> T load(Class<T> clazz, String id);
}
