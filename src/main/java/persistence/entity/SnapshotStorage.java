package persistence.entity;

public interface SnapshotStorage {

    void add(Object entity);

    void remove(EntityCacheKey entityCacheKey);

    boolean isDirty(Object entity);
}
