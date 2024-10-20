package persistence.entity;

public interface PersistenceContext {
    Object findEntity(EntityKey entityKey);

    void addEntity(EntityKey entityKey, Object entity);

    void removeEntity(EntityKey entityKey);

    static PersistenceContext getInstance() {
        return new PersistenceContextImpl();
    }
}
