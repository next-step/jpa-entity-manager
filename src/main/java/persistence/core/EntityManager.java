package persistence.core;

import persistence.entity.Person;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id);

   <T> T persist(T entity);

    void remove(Object entity);

}
