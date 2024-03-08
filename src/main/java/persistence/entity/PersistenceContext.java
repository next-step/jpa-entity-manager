package persistence.entity;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(Object entity);

    boolean isDirty(EntityKey id, Object entity);
}
