package persistence.entity.context;

public interface PersistenceContext {

    Object getEntity(Class<?> cls, Long id);

    void addEntity(Long id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Class<?> cls, Long id);

    void clear();
}
