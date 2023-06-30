package persistence.entity;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        // 트랜잭션은 신경 쓰지말고 구현해보자
        return null;
    }
}
