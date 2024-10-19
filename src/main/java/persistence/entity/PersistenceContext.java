package persistence.entity;

public interface PersistenceContext {
    Object findEntity(EntityKey id);

    void addEntity(EntityKey id, Object entity);

    void removeEntity(EntityKey id);

    static PersistenceContext getInstance() {
        return new PersistenceContextImpl();
    }
}
