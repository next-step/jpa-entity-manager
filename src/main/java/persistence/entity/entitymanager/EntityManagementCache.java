package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;
import persistence.entity.persistencecontext.DefaultPersistenceContext;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.sql.dbms.Dialect;

import java.util.HashMap;
import java.util.Map;

public class EntityManagementCache {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final Map<Class<?>, EntityPersister<?>> persisterCachePool;
    private final Map<Class<?>, EntityLoader<?>> loaderCachePool;
    private final Map<Class<?>, PersistenceContext<?>> persistenceCachePool;

    public EntityManagementCache(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.persisterCachePool = new HashMap<>();
        this.loaderCachePool = new HashMap<>();
        this.persistenceCachePool = new HashMap<>();
    }

    public <T> EntityPersister<T> persister(Class<T> entityClass) {
        if(!persisterCachePool.containsKey(entityClass)) {
            persisterCachePool.put(entityClass, new EntityPersister<>(entityClass, jdbcTemplate, dialect));
        }

        return (EntityPersister<T>) persisterCachePool.get(entityClass);
    }

    public <T> EntityLoader<T> loader(Class<T> entityClass) {
        if(!loaderCachePool.containsKey(entityClass)) {
            loaderCachePool.put(entityClass, new EntityLoader<>(jdbcTemplate, dialect));
        }

        return (EntityLoader<T>) loaderCachePool.get(entityClass);
    }

    public <T> PersistenceContext<T> persistenceContext(Class<T> entityClass) {
        if(!persistenceCachePool.containsKey(entityClass)) {
            persistenceCachePool.put(entityClass, new DefaultPersistenceContext());
        }

        return (PersistenceContext<T>) persistenceCachePool.get(entityClass);
    }
}
