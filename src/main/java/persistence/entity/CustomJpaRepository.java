package persistence.entity;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final Class<T> clazz;

    public CustomJpaRepository(final EntityManager entityManager, final Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    public T save(final T t) {
        entityManager.persist(t);
        return t;
    }

    public T findById(final ID id) {
        return entityManager.find(clazz, id);
    }
}
