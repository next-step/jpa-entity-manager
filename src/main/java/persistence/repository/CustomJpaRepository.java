package persistence.repository;

import persistence.entity.EntityManager;
import persistence.sql.entity.EntityData;
import util.ReflectionUtil;

public class CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        Class<?> entityClass = t.getClass();
        Object id = ReflectionUtil.getValueFrom(new EntityData(entityClass).getPrimaryKey().getField(), t);

        T found = (T) entityManager.find(entityClass, id);

        if (!found.equals(t)) {
            entityManager.persist(t);
        }
        return t;
    }

}
