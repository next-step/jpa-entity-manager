package persistence.defaulthibernate;

public interface PersistenceContext {

    void add(Object object, Long id);

    Object get(Class<?> clazz, Long id);

    void update(Object object, Long id);

    void remove(Class<?> clazz, Long id);

    Object getDatabaseSnapShot(Object object, Long id);

}
