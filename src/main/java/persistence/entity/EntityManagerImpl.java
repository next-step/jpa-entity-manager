package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.SelectByIdQueryBuilder;

import java.io.Serializable;

public class EntityManagerImpl implements EntityManager {

    private static final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = PersistenceContext.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final TableDefinition tableDefinition = new TableDefinition(clazz);
        final EntityKey entityKey = new EntityKey((Serializable) id, tableDefinition.entityName());
        final Object managedEntity = persistenceContext.findEntity(entityKey);

        return managedEntity != null ? clazz.cast(managedEntity) : queryForObject(clazz, entityKey);
    }

    private <T> T queryForObject(Class<T> clazz, EntityKey id) {
        final String query = selectByIdQueryBuilder.build(clazz, id.getId());
        T queried = jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));

        persistenceContext.addEntity(id, queried);
        return queried;
    }

    @Override
    public void persist(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entityPersister.getEntityName()
        );

        if (persistenceContext.findEntity(entityKey) != null) {
            return;
        }

        entityPersister.insert(entity);
        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entityPersister.getEntityName()
        );

        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public void update(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entityPersister.getEntityName()
        );

        entityPersister.update(entity);
        persistenceContext.addEntity(entityKey, entity);
    }
}
