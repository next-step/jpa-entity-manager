package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class EntityLoader {
    private final Logger log = LoggerFactory.getLogger(EntityLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;

    public EntityLoader(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }

    public <T> T find(Class<T> tClass, Object id) {
        final EntityMeta entityMeta = EntityMeta.from(tClass);
        final EntityMapper entityMapper = new EntityMapper(entityMeta);

        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findByIdQuery(id);

        log.info(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }

    public <T> List<T> findAll(Class<T> tClass) {
        final EntityMeta entityMeta = EntityMeta.from(tClass);
        final EntityMapper entityMapper = new EntityMapper(entityMeta);

        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findAllQuery();

        log.info(query);

        return jdbcTemplate.query(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }


}
