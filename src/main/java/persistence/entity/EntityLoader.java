package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.SelectQueryBuilder;
import persistence.sql.dml.model.DMLColumn;
import persistence.sql.model.Table;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T find(Class<T> clazz, Long id) {
        final String query = findQuery(clazz, id);

        return jdbcTemplate.queryForObject(query, new RowMapperImpl<>(clazz));
    }

    private <T> String findQuery(Class<T> clazz, Long id) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(
                new Table(clazz), new DMLColumn(clazz)
        );

        return queryBuilder.findById(id).build();
    }

}
