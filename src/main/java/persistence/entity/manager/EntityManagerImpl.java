package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.entity.exception.EntityExistsException;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.persister.EntityPersister;

import java.util.Optional;

import static persistence.entity.generator.PrimaryKeyValueGenerator.primaryKeyValue;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, EntityPersister entityPersister, EntityLoader entityLoader) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        var cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isPresent()) {
            return cachedEntity;
        }

        var searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        persistenceContext.addEntity(searchedEntity);
        return (Optional<T>) persistenceContext.getDatabaseSnapshot(searchedEntity, id);
    }

    @Override
    public <T> T persist(T entity) {
        validate(entity);
        var insertedEntity = entityPersister.insert(entity);
        return (T) persistenceContext.addEntity(insertedEntity);
    }

    private void validate(Object entity) {
        var primaryKey = primaryKeyValue(entity);
        var searchedEntity = persistenceContext.getEntity(entity.getClass(), primaryKey);

        if (searchedEntity.isPresent()) {
            throw new EntityExistsException();
        }
    }

    @Override
    public <T> T merge(T entity) {
        var primaryKey = primaryKeyValue(entity);
        var snapshot = persistenceContext.getDatabaseSnapshot(entity, primaryKey);

        if (snapshot != entity) {
            var updatedEntity = entityPersister.update(entity, primaryKey);
            return (T) persistenceContext.updateEntity(updatedEntity, primaryKey);
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
