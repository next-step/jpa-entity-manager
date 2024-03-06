package persistence.sql.entity.loader;

import java.util.List;

public interface EntityLoader {

    <T> List<T> findAll(Class<T> clazz);

    <T> T find(Class<T> clazz, Object id);

}
