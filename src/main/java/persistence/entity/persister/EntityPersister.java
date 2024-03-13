package persistence.entity.persister;

public interface EntityPersister {

    boolean update(final Object entity);

    Object insert(final Object entity);

    void delete(final Object entity);

    Object getIdentifier(final Object entity);

    void setIdentifier(final Object entity, final Object value);

}
