package repository;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.entity.DefaultEntityManager;
import persistence.entity.EntityManager;

public class BaseCrudRepository<T> extends AbstractRepository<T> implements CrudRepository<T> {
    private final EntityManager entityManager;
    protected BaseCrudRepository(JdbcTemplate jdbcTemplate, Class<T> tClass, Dialect dialect) {
        super(jdbcTemplate, tClass, dialect);
        this.entityManager = new DefaultEntityManager(jdbcTemplate, entityMeta, dialect);
    }

    public T save(T entity) {
        return (T) entityManager.persist(entity);
    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }


    public T findById(Class<T> tClass, Object id) {
        return entityManager.find(tClass, id);
    }

    public List<T> findAll() {
        return entityManager.findAll(tClass);
    }
}
