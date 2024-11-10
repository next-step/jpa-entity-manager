package persistence.entity;

public interface PersistenceContext {

    <T, ID> EntityEntry getEntity(ID id, Class<T> entityType);

    <T> void addEntity(T entity, EntityStatus status);
    <T, ID> EntityEntry addLoadingEntity(ID id, Class<T> entityType);

    void removeEntity(Object entity);

}
