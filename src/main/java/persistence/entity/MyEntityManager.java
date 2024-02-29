package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;
import utils.ValueExtractor;
import utils.ValueInjector;

public class MyEntityManager implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
        this.entityLoader = new MyEntityLoader(jdbcTemplate);
        this.persistenceContext = new MyPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            T foundEntity = entityLoader.find(clazz, id);
            addToCache(id, foundEntity);
            return foundEntity;
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        Table table = Table.from(entity.getClass());
        IdColumn idColumn = table.getIdColumn();
        Object generatedId = entityPersister.insert(entity);

        ValueInjector.inject(entity, idColumn, generatedId);
        addToCache(generatedId, entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        Table table = Table.from(entity.getClass());
        Object id = ValueExtractor.extract(entity, table.getIdColumn());
        EntitySnapshot snapshot = (EntitySnapshot) persistenceContext.getCachedDatabaseSnapshot(id, entity);
        if (snapshot.isChanged(entity)) {
            entityPersister.update(entity);
        }
        addToCache(id, entity);
        return entity;
    }

    private void addToCache(Object id, Object entity) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }
}
