package persistence.entity;

import persistence.sql.dialect.Dialect;

public interface EntityManager {

    <T> T find(Class<T> clazz, Object id);

    void persist(Object entity);

    void remove(Object entity);

    Dialect getDialect();

}
