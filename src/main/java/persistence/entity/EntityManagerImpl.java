package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteByIdQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.SelectByIdQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

public class EntityManagerImpl implements EntityManager {

    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final SelectByIdQueryBuilder selectByIdQueryBuilder = new SelectByIdQueryBuilder();
    private static final DeleteByIdQueryBuilder deleteByIdQueryBuilder = new DeleteByIdQueryBuilder();
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = PersistenceContext.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey(id);
        final Object managedEntity = persistenceContext.findEntity(entityKey);

        return managedEntity != null ? clazz.cast(managedEntity) : queryForObject(clazz, entityKey);
    }

    private <T> T queryForObject(Class<T> clazz, EntityKey id) {
        final String query = selectByIdQueryBuilder.build(clazz, id.value());
        T queried = jdbcTemplate.queryForObject(query, new GenericRowMapper<>(clazz));

        persistenceContext.addEntity(id, queried);
        return queried;
    }

    @Override
    public void persist(Object entity) {
        final String query = insertQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    @Override
    public void remove(Object entity) {
        final String query = deleteByIdQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

    @Override
    public void update(Object entity) {
        final String query = updateQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }
}
