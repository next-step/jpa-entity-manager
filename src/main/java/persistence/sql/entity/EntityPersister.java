package persistence.sql.entity;

public interface EntityPersister {

    boolean update(Object object);
    void insert(Object object);
    void delete(Object object);
}
