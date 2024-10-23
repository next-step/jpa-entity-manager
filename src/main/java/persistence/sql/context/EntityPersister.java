package persistence.sql.context;

public interface EntityPersister {

    <T> Object insert(T entity);

    <T> void update(T entity, T snapshotEntity);

    <T> void delete(T entity);
}
