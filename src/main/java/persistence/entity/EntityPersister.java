package persistence.entity;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class EntityPersister {
    private static final Logger log = LoggerFactory.getLogger(EntityPersister.class);
    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta entityMeta;
    private final Dialect dialect;

    public EntityPersister(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.dialect = dialect;
    }

    public void insert(Object entity) {
        final String query = QueryGenerator.of(entityMeta, dialect).insert(entity);
        log.info(query);
        jdbcTemplate.execute(query);
    }

    public boolean update(Object entity) {
        final String query = QueryGenerator.of(entityMeta, dialect).update(entity);
        log.info(query);
        jdbcTemplate.execute(query);
        return true;
    }

    public void delete(Object entity) {
        final String query = QueryGenerator.of(entityMeta, dialect).delete(entityMeta.getPkValue(entity));
        log.info(query);
        jdbcTemplate.execute(query);
    }

}
