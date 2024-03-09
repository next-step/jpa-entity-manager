package persistence.sql.entity;

public interface EntityPersister {

    boolean update(Object object);
    Long insert(Object object);
    void delete(Object object);
}
