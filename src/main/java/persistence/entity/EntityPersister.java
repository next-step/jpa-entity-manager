package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;


public class EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(EntityPersister.class);
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;
    private final DeleteQueryBuilder deleteQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;


    public EntityPersister(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, QueryGenerator queryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.deleteQueryBuilder = queryGenerator.delete();
        this.insertQueryBuilder = queryGenerator.insert();
        this.updateQueryBuilder = queryGenerator.update();
    }

    public void insert(Object entity) {
        final String query = insertQueryBuilder.build(entity);
        log.info(query);
        jdbcTemplate.execute(query);
    }

    public boolean update(Object entity) {
        final String query = updateQueryBuilder.build(entity);
        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        final String query = deleteQueryBuilder.build(entityMeta.getPkValue(entity));
        log.info(query);
        jdbcTemplate.execute(query);
    }

}
