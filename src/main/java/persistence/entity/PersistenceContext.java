package persistence.entity;

public interface PersistenceContext {
    Object getEntity(Integer id);

    void addEntity(Integer id, Object entity);

    void removeEntity(Integer id);

    boolean isValidEntity(Integer id);
}
