package persistence.entity.context;

public interface PersistenceContext<T> {

    Object getEntity(Long id);

    void addEntity(Long id, T entity);

    void removeEntity(T entity);
}
