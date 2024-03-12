package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.domain.EntityEntry;
import persistence.entity.domain.EntitySnapshot;
import persistence.entity.domain.EntityStatus;
import persistence.entity.persistence.PersistenceContext;
import persistence.entity.persistence.PersistenceContextImpl;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

import java.util.Objects;

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
            EntityEntry entityEntry = new EntityEntry(EntityStatus.LOADING);
            persistenceContext.addEntityEntry(entity, entityEntry);
            createCache(entity, id);
            entityEntry.updateStatus(EntityStatus.MANAGED);
            return entity;
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        EntityEntry entityEntry = new EntityEntry(EntityStatus.SAVING);
        Object id = entityPersister.insert(entity);
        Columns columns = new Columns(entity.getClass());
        columns.setPkValue(entity, id);
        persistenceContext.addEntityEntry(entity, entityEntry);
        createCache(entity, id);
        entityEntry.updateStatus(EntityStatus.MANAGED);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.updateStatus(EntityStatus.DELETED);
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
        entityEntry.updateStatus(EntityStatus.GONE);
    }

    @Override
    public <T> T merge(T entity) {
        Columns columns = new Columns(entity.getClass());
        Object id = new Value(columns.getPrimaryKeyColumn(), entity).getOriginValue();

        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(id, entity);
        EntitySnapshot after = new EntitySnapshot(entity);
        if (!Objects.equals(before.getSnapshot(), after.getSnapshot())) {
            entityPersister.update(entity);
            createCache(entity, id);
            persistenceContext.addEntityEntry(entity, new EntityEntry(EntityStatus.MANAGED));
        }
        return entity;
    }

    private <T> void createCache(T entity, Object id) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

}
