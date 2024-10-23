package persistence.sql.entity;

import java.sql.SQLException;

public interface EntityManager {
    <T> T find(Class<T> clazz, Long Id);

    Object persist(Object entity) throws SQLException;

    void remove(Object entity);

    Object update(Object entity);
}
