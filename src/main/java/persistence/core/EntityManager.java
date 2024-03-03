package persistence.core;

import persistence.entity.Person;

import java.sql.SQLException;
import java.util.List;

public interface EntityManager {

    <T> List<T> find(Class<T> clazz) throws Exception;

    <T> T find(Class<T> clazz, Long Id) throws Exception;

   <T> T persist(T entity) throws Exception;

    void remove(Object entity) throws Exception;

    void update(Object entity) throws Exception;

}
