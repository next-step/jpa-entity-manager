package persistence.repository;

import jdbc.JdbcTemplate;
import persistence.PrimaryKey;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;

import java.util.Optional;

public class CustomJpaRepository {
    private final EntityManager entityManager;
    public CustomJpaRepository(JdbcTemplate jdbcTemplate) {
        this.entityManager = new EntityManagerImpl(jdbcTemplate);
    }


    <T> T save (T entity) {
        boolean isInEntityManger = entityManager.find(entity.getClass(), new PrimaryKey(entity).value()).isPresent();

        if (isInEntityManger) {
           return entityManager.merge(entity);
        }
        return entityManager.persist(entity);
    }

    <T> Optional<T> find(Class<T> clazz, Long id) {
        return entityManager.find(clazz, id);
    }
}
