package persistence.entity.persistencecontext;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.entity.exception.UnableToChangeIdException;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.PrimaryKeyClause;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final EntityCache entityCache;
    private final Snapshot snapshot;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        if (id  == null) {
           return Optional.empty();
        }
        var cachedEntity = entityCache.get(clazz, id);
        if (cachedEntity.isPresent()) {
            return (Optional<T>) cachedEntity;
        }

        var searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        entityCache.put(searchedEntity.get());
        snapshot.put(searchedEntity.get());
        return searchedEntity;
    }

    @Override
    public Object addEntity(Object entity) {
        var insertedEntity = entityPersister.insert(entity);
        initInsertedEntityId(entity, insertedEntity);

        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    private void initInsertedEntityId(Object entity,
                                  Object insertedEntity) {
        var insertedEntityId = PrimaryKeyClause.primaryKeyValue(insertedEntity);

        var idField = Arrays.stream(entity.getClass().getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class)).findAny().get();
        idField.setAccessible(true);
        try {
            idField.set(entity, insertedEntityId);
        } catch (IllegalAccessException e) {
            throw new UnableToChangeIdException();
        }
    }


    @Override
    public Object updateEntity(Object entity, Long id) {
        var updatedEntity = entityPersister.update(entity, id);
        initInsertedEntityId(entity, updatedEntity);

        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        entityPersister.delete(entity);
        entityCache.remove(entity);
        snapshot.remove(entity);
    }

    @Override
    public Optional<Object> getDatabaseSnapshot(Object entity, Long id) {
        var result = snapshot.get(entity.getClass(), id);
        return Optional.of(result);
    }
}
