package persistence.core;

import persistence.entity.Person;

import java.sql.SQLException;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id) throws Exception;

   <T> T persist(T entity) throws SQLException;

    void remove(Object entity) throws Exception;

    void update(Object entity) throws Exception;

}
