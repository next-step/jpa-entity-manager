package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.core.PersistenceEnvironment;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;
import java.sql.SQLException;

public class SimpleEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final Connection connection;
    private boolean closed;
    private final EntityPersisterProvider entityPersisterProvider;
    private final DmlGenerator dmlGenerator;

    public SimpleEntityManager(final PersistenceEnvironment persistenceEnvironment) {
        this.connection = persistenceEnvironment.getConnection();
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.closed = false;
        this.dmlGenerator = persistenceEnvironment.getDmlGenerator();
        this.entityPersisterProvider = new EntityPersisterProvider(jdbcTemplate, dmlGenerator);
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        checkConnectionOpen();
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(clazz);
        final EntityLoader<T> entityLoader = new EntityLoader<>(clazz, entityPersister, jdbcTemplate, dmlGenerator);
        return entityLoader.loadById(id);
    }

    @Override
    public void persist(final Object entity) {
        checkConnectionOpen();
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        checkConnectionOpen();
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        entityPersister.delete(entity);
    }

    private void checkConnectionOpen() {
        if (this.closed) {
            throw new PersistenceException("DB와의 커넥션이 끊어졌습니다.");
        }
    }
}
