package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;
import persistence.sql.dml.SelectQueryBuilder;

public class EntityLoader {
    private final Logger log = LoggerFactory.getLogger(EntityLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectBuilder;
    private final EntityMapper entityMapper;

    public EntityLoader(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, QueryGenerator queryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMapper = new EntityMapper(entityMeta);
        this.selectBuilder = queryGenerator.select();

    }

    public <T> T find(Class<T> tClass, Object id) {
        final String query = selectBuilder.findByIdQuery(id);
        log.info(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }

    public <T> List<T> findAll(Class<T> tClass) {
        final String query = selectBuilder.findAllQuery();
        log.info(query);
        return jdbcTemplate.query(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }


}
