package hibernate.entity.persistencecontext;

public interface PersistenceContext {

    Object getEntity(EntityKey id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getDatabaseSnapshot(EntityKey id);
}
