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

    public PersistenceContextImpl() {
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
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
        return Optional.empty();
    }

    @Override
    public Object addEntity(Object entity) {
        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    @Override
    public Object updateEntity(Object entity, Long id) {
        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        entityCache.remove(entity);
        snapshot.remove(entity);
    }

    @Override
    public Optional<Object> getDatabaseSnapshot(Object entity, Long id) {
        var result = snapshot.get(entity.getClass(), id);
        return Optional.of(result);
    }
}
