package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.UpdateQueryBuilder;
import persistence.sql.dml.WhereQueryBuilder;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final WhereQueryBuilder whereQueryBuilder;

    public EntityPersister(JdbcTemplate jdbcTemplate, Dialect dialect, WhereQueryBuilder whereQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.whereQueryBuilder = whereQueryBuilder;
    }

    public void update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.builder()
                .dialect(dialect)
                .entity(entity)
                .build();

        jdbcTemplate.execute(updateQueryBuilder.generateQuery());
    }

    public void insert() {
    }

    public void delete() {
    }
}
