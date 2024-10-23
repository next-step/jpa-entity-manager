package persistence.entity;

public interface EntityLoader {
    <T> T load(Class<T> entityType, Object id);
}
