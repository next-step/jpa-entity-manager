package persistence.entity;

import jdbc.TransactionalJdbcTemplate;

public interface EntityManager {

    <T> T find(Class<T> entityClass, Object primaryKey);

    void persist(Object entity);

    void remove(Object entity);

    void flush() throws IllegalAccessException;

    TransactionalJdbcTemplate getTransaction();

}
