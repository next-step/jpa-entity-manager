package jdbc;

import persistence.entity.EntityManager;
import persistence.entity.EntityMetadata;

public class CustomJpaRepository<T> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        Long id = new EntityMetadata(entity).getIdValue();

        if (id == null) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(id, entity);
    }
}
