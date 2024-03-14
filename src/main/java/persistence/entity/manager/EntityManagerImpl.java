package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.entity.exception.EntityExistsException;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.entity.persister.EntityPersister;

import java.util.Optional;

import static persistence.sql.dml.value.PrimaryKeyValue.getPrimaryKeyValue;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }
    public EntityManagerImpl(PersistenceContext persistenceContext, EntityLoader entityLoader, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
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
        T addedEntity = persistenceContext.addEntity(searchedEntity.get());
        return Optional.of(addedEntity);
    }

    @Override
    public <T> T persist(T entity) {
        validate(entity);
        T insertedEntity = entityPersister.insert(entity);
        return persistenceContext.updateEntity(insertedEntity, getPrimaryKeyValue(insertedEntity));
    }

    private void validate(Object entity) {
        Long primaryKey = getPrimaryKeyValue(entity);
        Optional<?> searchedEntity = this.find(entity.getClass(), primaryKey);

        if (searchedEntity.isPresent()) {
            throw new EntityExistsException();
        }
    }

    @Override
    public <T> T merge(T entity) {
        Long primaryKey = getPrimaryKeyValue(entity);
        Optional<Object> snapshot = getSnapShot(entity, primaryKey);

        if (entity.equals(snapshot.get())) {
            T updatedEntity = entityPersister.update(entity, primaryKey);
            return persistenceContext.updateEntity(updatedEntity, primaryKey);
        }
        return entity;
    }

    private <T> Optional<Object> getSnapShot(T entity, Long primaryKey) {
        Optional<Object> snapshot = persistenceContext.getDatabaseSnapshot(entity, primaryKey);

        if (snapshot.isEmpty()) {
            Optional<?> searchedEntity = entityLoader.find(entity.getClass(), primaryKey);
            snapshot = Optional.of(persistenceContext.updateEntity(searchedEntity, primaryKey));
        }
        return snapshot;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
