package persistence.entity;

public interface EntityManger {
    <T> T find(Class<T> clazz, Long id);

    Object persist(Object entity);

    void remove(Object entity);
}
