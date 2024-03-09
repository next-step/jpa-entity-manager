package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, EntityId id);

    void addEntity(EntityId id, Object entity);

    void removeEntity(Object entity);

    boolean isCached(Object entity);

    Object getDatabaseSnapshot(EntityId id, Object entity);

    EntityEntry getEntityEntry(Object entity);

    EntityEntry getEntityEntry(Class<?> clazz, EntityId id);
}
