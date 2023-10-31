package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.PersistenceEnvironment;
import persistence.sql.dml.DmlGenerator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimpleEntityManagerFactory implements EntityManagerFactory {
    private final EntityPersisterProvider entityPersisterProvider;
    private final EntityLoaderProvider entityLoaderProvider;

    public SimpleEntityManagerFactory(final EntityScanner entityScanner, final PersistenceEnvironment persistenceEnvironment) {
        final List<Class<?>> entityClasses = entityScanner.getEntityClasses();
        final DmlGenerator dmlGenerator = persistenceEnvironment.getDmlGenerator();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(persistenceEnvironment.getConnection());

        final Map<Class<?>, EntityPersister> persisters = createEntityPersisters(entityClasses, dmlGenerator, jdbcTemplate);
        final Map<Class<?>, EntityLoader<?>> loaders = createEntityLoaders(entityClasses, dmlGenerator, jdbcTemplate);

        this.entityPersisterProvider = new EntityPersisterProvider(persisters);
        this.entityLoaderProvider = new EntityLoaderProvider(loaders);
    }

    private Map<Class<?>, EntityPersister> createEntityPersisters(final List<Class<?>> entityClasses, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> new EntityPersister(clazz, dmlGenerator, jdbcTemplate)
                ));
    }

    private Map<Class<?>, EntityLoader<?>> createEntityLoaders(final List<Class<?>> entityClasses, final DmlGenerator dmlGenerator, final JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        clazz -> new EntityLoader<>(clazz, dmlGenerator, jdbcTemplate)
                ));
    }

    @Override
    public EntityManager createEntityManager() {
        return new SimpleEntityManager(this.entityPersisterProvider, this.entityLoaderProvider);
    }
}

