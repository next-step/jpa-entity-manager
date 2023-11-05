package persistence.entity;

public interface EventSource {

    void putEntity(Object id, Object entity);

    void purgeEntity(Object entity);

    void loading(Object object);

    void saving(Object object);

    void managed(Object object);

    void readOnly(Object object);

    void deleted(Object object);

    void gone(Object object);

    EntityEntry getEntityEntry(Object object);
}
