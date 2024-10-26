package persistence;


public interface EntityManager {

    <T> T find(Class<T> clazz, Object id);

    void persist(Object entityInstance);

    void merge(Object entityInstance);

    void remove(Object entityInstance);

}
