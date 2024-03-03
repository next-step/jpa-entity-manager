package persistence.entity;

import persistence.sql.dialect.Dialect;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long id);

    <T> T persist(Object entity);

    void remove(Object entity);

    <T> T merge(T entity);

    Dialect getDialect();

    void flush();
}
