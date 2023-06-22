package persistence.context;

public interface PersistenceContext {
    Object getEntity(Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Long id);
}
