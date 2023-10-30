package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.QueryGenerator;

public class EntityLoader {
    private final Logger log = LoggerFactory.getLogger(EntityLoader.class);
    private final JdbcTemplate jdbcTemplate;
    private final QueryGenerator queryGenerator;
    private final EntityMapper entityMapper;

    public EntityLoader(JdbcTemplate jdbcTemplate, QueryGenerator queryGenerator, EntityMapper entityMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryGenerator = queryGenerator;
        this.entityMapper = entityMapper;
    }

    public <T> T find(Class<T> tClass, Object id) {
        final String query = queryGenerator
                .select()
                .findByIdQuery(id);

        log.info(query);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }

    public <T> List<T> findAll(Class<T> tClass) {
        final String query = queryGenerator.select().findAllQuery();

        log.info(query);

        return jdbcTemplate.query(query,
                (resultSet) -> entityMapper.resultSetToEntity(tClass, resultSet));
    }


}
