package persistence.entity;

public interface EntityLoader {
    <T> T find(Class<T> entityType, Object id);
}
