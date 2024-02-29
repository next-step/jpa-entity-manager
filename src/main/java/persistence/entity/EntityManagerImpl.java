package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuild;
import persistence.sql.dml.InsertQueryBuild;
import persistence.sql.dml.SelectQueryBuild;
import persistence.sql.dml.UpdateQueryBuild;
import persistence.sql.domain.Query;
import persistence.sql.domain.QueryResult;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    private final SelectQueryBuild selectQueryBuilder;

    private final JdbcTemplate jdbcTemplate;


    public EntityManagerImpl(JdbcTemplate jdbcTemplate,
                             UpdateQueryBuild updateQueryBuilder,
                             InsertQueryBuild insertQueryBuilder,
                             DeleteQueryBuild deleteQueryBuilder,
                             SelectQueryBuild selectQueryBuilder) {
        this.entityPersister = new EntityPersister(jdbcTemplate, insertQueryBuilder, updateQueryBuilder, deleteQueryBuilder);
        this.selectQueryBuilder = selectQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public boolean update(Object entity, Object id) {
        return entityPersister.update(entity, id);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        Query query = selectQueryBuilder.findById(clazz, id);

        return jdbcTemplate.queryForObject(query.getSql(), new QueryResult<>(query.getTable(), clazz));
    }

}
