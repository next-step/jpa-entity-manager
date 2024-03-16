package jdbc;

import persistence.EntityManager;
import persistence.IdMetadata;

public class CustomJpaRepository<T> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        Long id = new IdMetadata(entity).getMetadata();

        if (id == null) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(id, entity);
    }
}
