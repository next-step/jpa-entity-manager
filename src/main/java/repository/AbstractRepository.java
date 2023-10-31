package repository;

import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;

public abstract class AbstractRepository<T, ID> {

    protected final JdbcTemplate jdbcTemplate;
    protected final Class<T> tClass;

    protected final EntityMeta entityMeta;

    protected final Dialect dialect;

    protected AbstractRepository(JdbcTemplate jdbcTemplate, Class<T> tClass, Dialect dialect) {
        this.tClass = tClass;
        this.entityMeta = EntityMeta.from(tClass);
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
    }
}
