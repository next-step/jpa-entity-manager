package persistence.entity;


import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;


public class DefaultEntityManager implements EntityManager {
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, Dialect dialect) {
        this.entityLoader = new EntityLoader(jdbcTemplate, entityMeta, dialect);
        this.entityPersister = new EntityPersister(jdbcTemplate, entityMeta, dialect);
    }

    @Override
    public <T> T persist(T entity) {
        entityPersister.insert(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        return entityLoader.findAll(tClass);
    }


}
