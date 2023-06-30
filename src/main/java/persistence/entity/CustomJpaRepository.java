package persistence.entity;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) throws IllegalAccessException {
        if (entityManager.isChanged(t)) {
            entityManager.persist(t);
        }

        return t;
    }

}
