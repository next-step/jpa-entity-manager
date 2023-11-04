package persistence.entity;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class EntityManagerFactory {
    private final EntityPersisteContext entityPersisteContext;
    private final EntityLoaderContext entityLoaderContext;

    private EntityManagerFactory(Set<Class<?>> entityClasses, JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityPersisteContext = initEntityPersisteContext(entityClasses, jdbcTemplate, dialect);
        this.entityLoaderContext = initEntityLoaderContext(entityClasses, jdbcTemplate, dialect);
    }

    public static EntityManagerFactory of(String packageName, JdbcTemplate jdbcTemplate, Dialect dialect) {
        return new EntityManagerFactory(getEntityClassesFromPackage(packageName), jdbcTemplate, dialect);
    }

    private EntityPersisteContext initEntityPersisteContext(Set<Class<?>> entityClasses, JdbcTemplate jdbcTemplate,
                                                            Dialect dialect) {
        Map<Class<?>, EntityPersister> persiterContext = new ConcurrentHashMap<>();
        for (Class<?> clazz : entityClasses) {
            EntityMeta entityMeta = EntityMeta.from(clazz);
            QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
            persiterContext.put(clazz, new EntityPersister(jdbcTemplate, entityMeta, queryGenerator));
        }
        return new EntityPersisteContext(persiterContext);
    }

    private EntityLoaderContext initEntityLoaderContext(Set<Class<?>> entityClasses, JdbcTemplate jdbcTemplate,
                                                        Dialect dialect) {
        Map<Class<?>, EntityLoader> loaderContext = new ConcurrentHashMap<>();
        for (Class<?> clazz : entityClasses) {
            EntityMeta entityMeta = EntityMeta.from(clazz);
            QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
            loaderContext.put(clazz, new EntityLoader(jdbcTemplate, queryGenerator, new EntityMapper(entityMeta)));
        }
        return new EntityLoaderContext(loaderContext);
    }

    private static Set<Class<?>> getEntityClassesFromPackage(String packageName) {
        return new EntityClassLoader(packageName).getEntityClasses();
    }

    public EntityManager createEntityManager() {
        return SimpleEntityManager.of(entityPersisteContext, entityLoaderContext);
    }
}
