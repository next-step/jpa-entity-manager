package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

public class MyEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public MyEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = InsertQueryBuilder.getInstance();
        this.updateQueryBuilder = UpdateQueryBuilder.getInstance();
        this.deleteQueryBuilder = DeleteQueryBuilder.getInstance();
    }

    @Override
    public boolean update(EntityMeta entityMeta) {
        String query = updateQueryBuilder.build(entityMeta.getEntity());
        return jdbcTemplate.executeForUpdate(query);
    }

    @Override
    public Object insert(EntityMeta entity) {
        String query = insertQueryBuilder.build(entity.getEntity());
        return jdbcTemplate.executeForInsert(query);
    }

    @Override
    public void delete(EntityMeta entity) {
        String query = deleteQueryBuilder.build(entity.getEntity());
        jdbcTemplate.execute(query);
    }
}
