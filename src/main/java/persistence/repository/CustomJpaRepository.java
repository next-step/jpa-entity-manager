package persistence.repository;

import jdbc.JdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.persistencecontext.PersistenceContext;

public class CustomJpaRepository {
    private final EntityManager entityManager;
    public CustomJpaRepository(JdbcTemplate jdbcTemplate) {
        this.entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    public CustomJpaRepository(PersistenceContext persistenceContext) {
        this.entityManager = new EntityManagerImpl(persistenceContext);
    }

    <T> T save (T entity) {
        return (T) entityManager.persist(entity);
    }
}
