package persistence.entity;

import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.domain.Query;

import java.sql.Connection;
import java.sql.Statement;

public class EntityPersister {

    private final Connection connection;

    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityPersister(Connection connection, DmlQueryBuilder dmlQueryBuilder) {
        this.connection = connection;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public <T> boolean update(T entity, Object id) {
        Query query = dmlQueryBuilder.update(entity, id);
        try {
            executeQuery(query);
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }

    public <T> void insert(T entity) {
        Query query = dmlQueryBuilder.insert(entity);
        executeQuery(query);
    }

    public <T> void delete(T entity) {
        Query query = dmlQueryBuilder.delete(entity);
        executeQuery(query);
    }

    private void executeQuery(Query query) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(query.getSql());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
