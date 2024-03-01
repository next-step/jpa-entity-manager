package persistence.entity.persister;

public interface EntityPersister {

    boolean update(Object entity);
    void insert(Object entity);
    void delete(Object entity);
}
