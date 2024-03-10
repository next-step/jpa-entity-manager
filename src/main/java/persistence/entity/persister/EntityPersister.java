package persistence.entity.persister;

public interface EntityPersister {

    boolean update(final Object entity);

    Object insert(final Object entity);

    void delete(final Object entity);

    Object getIdentifier(Object entity);

}
