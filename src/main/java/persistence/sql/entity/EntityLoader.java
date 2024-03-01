package persistence.sql.entity;

public interface EntityLoader {

    <T> T findById(Class<T> clazz, Long id);
}
