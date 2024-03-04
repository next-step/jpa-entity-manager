package persistence.entity;

import java.util.List;

public interface EntityLoader {

    <T> T findById(Class<T> clazz, Long id);

    <T> List<T> findAll(Class<T> clazz);
}
