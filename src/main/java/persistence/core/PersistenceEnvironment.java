package persistence.core;

import database.DatabaseServer;
import persistence.dialect.Dialect;
import persistence.entity.EntityLoaderProvider;
import persistence.entity.EntityPersisterProvider;
import persistence.exception.PersistenceException;
import persistence.sql.dml.DmlGenerator;

import java.sql.Connection;
import java.sql.SQLException;

public class PersistenceEnvironment {
    private final DatabaseServer server;
    private final Dialect dialect;
    private final DmlGenerator dmlGenerator;
    private final EntityPersisterProvider entityPersisterProvider;
    private final EntityLoaderProvider entityLoaderProvider;

    public PersistenceEnvironment(final DatabaseServer server, final Dialect dialect) {
        this.server = server;
        this.dialect = dialect;
        this.dmlGenerator = new DmlGenerator(dialect);
        this.entityPersisterProvider = new EntityPersisterProvider(this);
        this.entityLoaderProvider = new EntityLoaderProvider(this);
    }

    public Dialect getDialect() {
        return this.dialect;
    }

    public Connection getConnection() {
        try {
            return this.server.getConnection();
        } catch (final SQLException e) {
            throw new PersistenceException("커넥션 연결을 실패했습니다.", e);
        }
    }

    public DmlGenerator getDmlGenerator() {
        return this.dmlGenerator;
    }

    public EntityPersisterProvider getEntityPersisterProvider() {
        return this.entityPersisterProvider;
    }

    public EntityLoaderProvider getEntityLoaderProvider() {
        return this.entityLoaderProvider;
    }
}
