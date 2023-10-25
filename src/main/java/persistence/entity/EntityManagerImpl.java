package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcTemplate;
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

    private final ColumnType columnType;

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(Connection connection, ColumnType columnType) {
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
}
