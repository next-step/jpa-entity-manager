package persistence.entity.database;

import database.mapping.EntityClass;
import database.mapping.EntityMetadata;
import database.sql.dml.SelectByPrimaryKeyQueryBuilder;
import database.sql.dml.SelectQueryBuilder;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Object> load(Class<?> clazz, Collection<Long> ids) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();
        RowMapper<Object> rowMapper = entityClass.getRowMapper();

        String query = new SelectQueryBuilder(metadata).buildQuery(Map.of("id", ids));
        return jdbcTemplate.query(query, rowMapper);
    }

    public Optional<Object> load(Class<?> clazz, Long id) {
        EntityClass entityClass = EntityClass.of(clazz);
        EntityMetadata metadata = entityClass.getMetadata();
        RowMapper<Object> rowMapper = entityClass.getRowMapper();

        String query = new SelectByPrimaryKeyQueryBuilder(metadata).buildQuery(id);
        return jdbcTemplate.query(query, rowMapper).stream().findFirst();
    }
}
