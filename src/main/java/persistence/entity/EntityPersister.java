package persistence.entity;

import jdbc.JdbcTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(EntityPersister.class);
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;

    public EntityPersister(JdbcTemplate jdbcTemplate, EntityMeta entityMeta) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
    }

    public void insert(Object entity) {
        final String query = QueryGenerator.from(entityMeta).insert(entity);
        log.info(query);
        jdbcTemplate.execute(query);
    }

    public boolean update(Object entity) {
        final String query = QueryGenerator.from(entityMeta).update(entity);
        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        final String query = QueryGenerator.from(entityMeta).delete(entityMeta.getPkValue(entity));
        log.info(query);
        jdbcTemplate.execute(query);
    }

}
