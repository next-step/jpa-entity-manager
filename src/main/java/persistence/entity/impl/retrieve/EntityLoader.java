package persistence.entity.impl.retrieve;

public interface EntityLoader {

    <T> T load(Class<T> clazz, Object id);
}
