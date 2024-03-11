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

    @Override
    public <T> Optional<T> find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        // TODO: getEntity parameters 단순화하기
        var primaryKey = PrimaryKeyClause.primaryKeyValue(entity);
        var searchedEntity = persistenceContext.getEntity(entity.getClass(), primaryKey);

        if (searchedEntity.isEmpty()) {
            persistenceContext.addEntity(entity);
            return;
        }

       var snapshot = persistenceContext.getDatabaseSnapshot(entity.getClass(), primaryKey);

        if (snapshot != searchedEntity) {
            entityPersister.update(entity, primaryKey);
        }
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
