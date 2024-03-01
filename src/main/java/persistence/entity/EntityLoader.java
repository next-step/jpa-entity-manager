package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.util.List;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public EntityLoader(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
    }

    public <T> T findOne(T entity, Long id) {
        List<T> entities = (List<T>) jdbcTemplate.query(
                selectQueryBuilder.findById(entity.getClass(), id),
                new RowMapperImpl<>(entity.getClass())
        );
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    public <T> T findOneOrFail(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new RowMapperImpl<>(clazz));
    }

}
