package persistence.entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        return (T) entityManager.persist(t);
    }

    public T findById(ID id) {
        return entityManager.find(getEntityClass(), (Long) id);
    }

    private Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
