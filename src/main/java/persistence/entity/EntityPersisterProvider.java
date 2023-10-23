package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityPersisterProvider {

    private final Map<Class<?>, EntityPersister> cache;
    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    public EntityPersisterProvider(final JdbcTemplate jdbcTemplate, final DmlGenerator dmlGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
        this.cache = new ConcurrentHashMap<>();
    }

    public EntityPersister getEntityPersister(final Class<?> clazz) {
        return cache.computeIfAbsent(clazz, cls->
            new EntityPersister(clazz, jdbcTemplate, dmlGenerator)
        );
    }

}
