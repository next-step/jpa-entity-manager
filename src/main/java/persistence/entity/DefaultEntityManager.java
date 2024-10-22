package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.meta.EntityTable;

import java.util.Objects;

public class DefaultEntityManager implements EntityManager {
    public static final String NOT_PERSISTENCE_CONTEXT_ENTITY_FAILD_MESSAGE = "영속성 컨텍스트에서 관리되는 엔티티가 아닙니다.";

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister,
                                 EntityLoader entityLoader) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(
                new DefaultPersistenceContext(),
                new DefaultEntityPersister(jdbcTemplate, new InsertQueryBuilder(), new UpdateQueryBuilder(), new DeleteQueryBuilder()),
                new DefaultEntityLoader(jdbcTemplate, new SelectQueryBuilder())
        );
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final T managedEntity = persistenceContext.getEntity(entityType, id);
        if (Objects.nonNull(managedEntity)) {
            return managedEntity;
        }

        final T entity = entityLoader.load(entityType, id);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void update(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        final Object snapshot = persistenceContext.getSnapshot(entity.getClass(), entityTable.getIdValue());

        if (Objects.isNull(snapshot)) {
            throw new IllegalStateException(NOT_PERSISTENCE_CONTEXT_ENTITY_FAILD_MESSAGE);
        }

        entityPersister.update(entity, snapshot);
        persistenceContext.addEntity(entity);
    }
}
