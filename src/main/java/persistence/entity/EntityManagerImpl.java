package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.persistence.PersistenceContext;
import persistence.entity.persistence.PersistenceContextImpl;
import persistence.sql.ddl.domain.Columns;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new EntityPersisterImpl(jdbcTemplate);
        this.entityLoader = new EntityLoaderImpl(jdbcTemplate);
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
            persistenceContext.getDatabaseSnapshot(id, entity);
            return entity;
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Object id = entityPersister.insert(entity);
        Columns columns = new Columns(entity.getClass());
        columns.setPkValue(entity, id);
        createCache(entity, id);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    private <T> void createCache(T entity, Object id) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

}
