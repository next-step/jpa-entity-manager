package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    boolean isDirty(Object entity);

    EntityEntry getEntry(Object entity);

    void addEntry(Object entity, EntityEntry entry);
}

