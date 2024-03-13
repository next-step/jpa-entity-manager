package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.entity.exception.EntityExistsException;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public Object persist(Object entity) {
        var primaryKey = PrimaryKeyClause.primaryKeyValue(entity);
        var searchedEntity = persistenceContext.getEntity(entity.getClass(), primaryKey);

        if (searchedEntity.isPresent()) {
            throw new EntityExistsException();
        }
        return persistenceContext.addEntity(entity);
    }

    @Override
    public Object merge(Object entity) {
        var primaryKey = PrimaryKeyClause.primaryKeyValue(entity);
        var snapshot = persistenceContext.getDatabaseSnapshot(entity, primaryKey);

        if (snapshot != entity) {
            return persistenceContext.updateEntity(entity, primaryKey);
        }
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
