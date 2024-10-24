package orm;

public interface EntityPersister {

    <T> T persist(T entity);

    <T> T update(T entity);

    void remove(Object entity);

}
