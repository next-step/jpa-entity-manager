package persistence.entity;

public interface EntityManger {
    <T> T find(Class<T> clazz, Object id);

    Object persist(Object entity);

    Object merge(Object entity);

    void remove(Object entity);
}
