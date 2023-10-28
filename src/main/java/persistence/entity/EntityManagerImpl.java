package persistence.entity;

import java.sql.Connection;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersister;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.dialect.ColumnType;
import persistence.sql.dml.clause.WherePredicate;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.statement.DeleteStatementBuilder;
import persistence.sql.dml.statement.InsertStatementBuilder;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class EntityManagerImpl implements EntityManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ColumnType columnType;
    private final Connection connection;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(Connection connection, ColumnType columnType) {
        this.connection = connection;
        this.columnType = columnType;
        this.entityLoader = new EntityLoaderImpl(connection, columnType);
        this.entityPersister = new EntityPersisterImpl(connection);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.load(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entity.getClass(), columnType);
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, classMappingMeta);

        if (objectMappingMeta.getIdValue() == null) {
            entityPersister.store(entity, columnType);
            return;
        }

        entityPersister.update(entity, columnType);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity, columnType);
    }

    @Override
    public void close() throws Exception {
        try {
            if (this.connection == null) {
                return;
            }

            if (this.connection.isClosed()) {
                return;
            }

            this.connection.close();
            log.info("EntityManager closed");
        } catch (SQLException e) {
            log.error("EntityManager connection not closed", e);
            throw new RuntimeException(e);
        }
    }
}
