package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.entity.persistencecontext.PersistenceContextImpl;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.Optional;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    public EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public Object persist(Object entity) {
        // TODO: getEntity parameters 단순화하기
        var primaryKey = PrimaryKeyClause.primaryKeyValue(entity);
        var searchedEntity = persistenceContext.getEntity(entity.getClass(), primaryKey);

        if (searchedEntity.isEmpty()) {
            return persistenceContext.addEntity(entity);
        }

       var snapshot = persistenceContext.getDatabaseSnapshot(entity, primaryKey);

        if (snapshot != entity) {
            return persistenceContext.updateEntity(entity, primaryKey);
        }
        throw new IllegalStateException("persist시 insert 혹은 update가 실행되어야합니다.");
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
