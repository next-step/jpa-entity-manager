package persistence.repository;

import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;

public class CustomJpaRepository {
    private final EntityManager entityManager;
    public CustomJpaRepository(JdbcTemplate jdbcTemplate) {
        this.entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    <T> T save (T entity) {
        return (T) entityManager.persist(entity);
    }
}
