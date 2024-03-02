package persistence.entity;

public interface EntityPersister {

    boolean update(Object entity, Object id);

    <T> long insertByGeneratedKey(T entity);

    <T> void insert(T entity);

    void delete(Object entity, Object id);
}
