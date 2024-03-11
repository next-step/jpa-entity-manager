package persistence.entity.manager;

import jdbc.JdbcTemplate;
import persistence.entity.exception.FailedPersistException;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl(jdbcTemplate);
    }

    public EntityManagerImpl(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public Object persist(Object entity) {
        var primaryKey = PrimaryKeyClause.primaryKeyValue(entity);
        var searchedEntity = persistenceContext.getEntity(entity.getClass(), primaryKey);

        if (searchedEntity.isEmpty()) {
            return persistenceContext.addEntity(entity);
        }

       var snapshot = persistenceContext.getDatabaseSnapshot(entity, primaryKey);

        if (snapshot != entity) {
            return persistenceContext.updateEntity(entity, primaryKey);
        }
        throw new FailedPersistException();
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
