package persistence.core;

public interface EntityLoader {

    <T> T find(Class<T> clazz, Long id);

}
