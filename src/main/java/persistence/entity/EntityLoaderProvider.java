package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityLoaderProvider {

    private final Map<Class<?>, EntityLoader<?>> cache;
    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersisterProvider entityPersisterProvider;

    public EntityLoaderProvider(final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate, final EntityPersisterProvider entityPersisterProvider) {
        this.dmlGenerator = dmlGenerator;
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersisterProvider = entityPersisterProvider;
        this.cache = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(final Class<T> clazz) {
        return (EntityLoader<T>) cache.computeIfAbsent(clazz, cls ->
                new EntityLoader<>(clazz, dmlGenerator, jdbcTemplate, entityPersisterProvider.getEntityPersister(clazz))
        );
    }

}
