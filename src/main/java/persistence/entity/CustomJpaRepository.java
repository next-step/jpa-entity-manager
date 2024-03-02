package persistence.entity;

public class CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;

    private final EntityInformation entityInformation;

    public CustomJpaRepository(EntityManager entityManager, EntityInformation entityInformation) {
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
    }

    public T save(T entity) {
        if (entityInformation.isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }
}
