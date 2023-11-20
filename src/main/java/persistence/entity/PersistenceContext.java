package persistence.entity;

public interface PersistenceContext {

    Object getEntity(Long id);

    Object getDatabaseSnapshot(Long id, Object entity);

    void addEntity(Long id, Object entity, Status status);

    void addEntitySnapshot(Long id, Object entity);

    void removeEntity(Object entity);

    void changeEntityStatus(Object entity, Status status);

    Status getEntityStatus(Object entity);

}
