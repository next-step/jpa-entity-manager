package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersisterProvider {

    private final Map<Class<?>, EntityPersister> cache;
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterProvider(final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.cache = new ConcurrentHashMap<>();
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.computeIfAbsent(clazz, cls ->
                new EntityPersister(clazz, dmlGenerator, jdbcTemplate)
        );
    }

}
