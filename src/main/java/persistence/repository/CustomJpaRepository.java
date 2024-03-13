package persistence.repository;

import jdbc.JdbcTemplate;
import persistence.entity.manager.EntityManager;
import persistence.entity.manager.EntityManagerImpl;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.Optional;

public class CustomJpaRepository {
    private final EntityManager entityManager;
    public CustomJpaRepository(JdbcTemplate jdbcTemplate) {
        this.entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    <T> T save (T entity) {
        var isInEntityManger = entityManager.find(entity.getClass(), PrimaryKeyClause.primaryKeyValue(entity)).isPresent();

        if (isInEntityManger) {
           return (T) entityManager.merge(entity);
        }
        return (T) entityManager.persist(entity);
    }

    <T> Optional<T> find(Class<T> clazz, Long id) {
        return entityManager.find(clazz, id);
    }
}
