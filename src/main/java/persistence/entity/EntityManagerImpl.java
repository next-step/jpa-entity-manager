package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.persistence.PersistenceContext;
import persistence.entity.persistence.PersistenceContextImpl;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

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
    public <T> T find(Class<T> clazz, Long Id) {
        T entity = persistenceContext.getEntity(clazz, Id);
        if (entity == null) {
            entity = entityLoader.find(clazz, Id);
            persistenceContext.addEntity(Id, entity);
            return entity;
        }
        return entity;
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        Columns columns = new Columns(entity.getClass());
        Value value = new Value(columns.getPrimaryKeyColumn(), entity);
        persistenceContext.addEntity(value.getValue(), entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
    
}
