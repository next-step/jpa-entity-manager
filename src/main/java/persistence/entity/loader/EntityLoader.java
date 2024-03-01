package persistence.entity.loader;

public interface EntityLoader {

    <T> T find(Class<T> clazz, Long id);
}
