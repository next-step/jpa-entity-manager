package hibernate.entity;

public interface PersistenceContext {

    Object getEntity(Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);
}
