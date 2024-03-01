package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.SelectQueryBuilder;

import java.util.List;
import java.util.Optional;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public EntityLoader(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
    }

    public <T> Optional<T> findOne(Class<T> clazz, Long id) {
        List<T> entities = jdbcTemplate.query(
                selectQueryBuilder.findById(clazz, id),
                new RowMapperImpl<>(clazz)
        );
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(entities.get(0));
    }

    public <T> Optional<T> findOne(T entity, Long id) {
        List<?> entities = jdbcTemplate.query(
                selectQueryBuilder.findById(entity.getClass(), id),
                new RowMapperImpl<>(entity.getClass()));
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return (Optional<T>) Optional.ofNullable(entities.get(0));
    }
}
