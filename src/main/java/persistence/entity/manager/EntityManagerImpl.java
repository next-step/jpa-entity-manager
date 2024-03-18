package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.PrimaryKey;
import persistence.entity.exception.EntityExistsException;
import persistence.entity.exception.ReadOnlyException;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persistencecontext.EntityEntry;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.persister.EntityPersister;

import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }
    public EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        Optional<T> cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isPresent()) {
            return cachedEntity;
        }

        Optional<T> searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        EntityEntry entityEntry = persistenceContext.getEntityEntry(clazz, id).get();
        entityEntry.load();
        T addedEntity = persistenceContext.addEntity(searchedEntity.get(), id);
        entityEntry.finishStatusUpdate();
        return Optional.of(addedEntity);
    }

    @Override
    public <T> T persist(T entity) {
        validate(entity);
        T insertedEntity = entityPersister.insert(entity);
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.save();
        entityEntry.finishStatusUpdate();
        return persistenceContext.updateEntity(insertedEntity, new PrimaryKey(insertedEntity).value());
    }

    private void validate(Object entity) {
        Long primaryKey = new PrimaryKey(entity).value();

        Optional<?> searchedEntityEntry = persistenceContext.getEntityEntry(entity.getClass(), primaryKey);
        if (searchedEntityEntry.isPresent()) {
            throw new EntityExistsException();
        }
    }

    @Override
    public <T> T merge(T entity) {

        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new ReadOnlyException();
        }

        Long primaryKey = new PrimaryKey(entity).value();

        if (persistenceContext.isDirty(entity)) {
            entityEntry.save();
            T updatedEntity = entityPersister.update(entity, primaryKey);
            T result = persistenceContext.updateEntity(updatedEntity, primaryKey);
            entityEntry.finishStatusUpdate();
            return result;
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.removeFromPersistenceContext();
        entityPersister.delete(entity);
        entityEntry.removeFromDatabase();
    }
}
