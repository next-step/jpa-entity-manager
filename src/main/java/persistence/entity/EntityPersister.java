package persistence.entity;

public interface EntityPersister {

    boolean update(final Object entity);

    void insert(final Object entity);

    void delete(final Object entity);

}
