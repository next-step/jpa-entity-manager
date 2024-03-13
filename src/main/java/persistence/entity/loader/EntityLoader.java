package persistence.entity.loader;

public interface EntityLoader {

    <T> T load(final Class<T> clazz, final Object key);

}
