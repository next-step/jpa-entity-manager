package persistence.entity;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        if (entityManager.isDirty(t)) {
            entityManager.merge(t);
        }
        return t;
    }
}
