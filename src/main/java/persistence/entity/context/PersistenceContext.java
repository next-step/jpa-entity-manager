package persistence.entity.context;

public interface PersistenceContext<T> {

    T getEntity(Long id);

    void addEntity(Long id, T entity);

    void removeEntity(T entity);

    T getDatabaseSnapshot(Long id);
}
