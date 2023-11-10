package persistence.entity;

public interface PersistenceContext {

    Object getEntity(Long id);

    Object getDatabaseSnapshot(Long id, Object entity);

    void addEntity(Long id, Object entity);

    void addEntitySnapshot(Long id, Object entity);

    void removeEntity(Object entity);

}
