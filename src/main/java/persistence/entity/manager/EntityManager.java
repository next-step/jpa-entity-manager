package persistence.entity.manager;

public interface EntityManager {

    <T> T findById(Class<T> clazz, String Id);

    <T> T persist(T entity);

    <T> void remove(T entity);

    <T, ID> T getDatabaseSnapshot(ID id, T entity);

    <T, ID> T getCachedDatabaseSnapshot(Class<T> clazz, ID id);
}
