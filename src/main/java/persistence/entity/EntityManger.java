package persistence.entity;

import jdbc.TransactionalJdbcTemplate;

public interface EntityManger {

    <T> T find(Class<T> entityClass, Object primaryKey);

    void persist(Object entity);

    void remove(Object entity);

    void update(Object entity) throws IllegalAccessException;

    void flush() throws IllegalAccessException;

    void detach(Object entity);

    TransactionalJdbcTemplate getTransaction();

}
