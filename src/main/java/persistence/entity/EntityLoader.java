package persistence.entity;

import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.domain.Query;
import persistence.sql.domain.QueryResult;

import java.sql.Connection;
import java.sql.ResultSet;

public class EntityLoader {

    private final Connection connection;

    private final DmlQueryBuilder dmlQueryBuilder;

    public EntityLoader(Connection connection, DmlQueryBuilder dmlQueryBuilder) {
        this.connection = connection;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    public <T> T find(Class<T> clazz, Object id) {
        Query query = dmlQueryBuilder.findById(clazz, id);

        return executeQueryForEntity(clazz, query);
    }

    private <T> T executeQueryForEntity(Class<T> clazz, Query query) {
        try (final ResultSet resultSet = connection.prepareStatement(query.getSql()).executeQuery()) {
            QueryResult queryResult = new QueryResult(resultSet, query.getTable());
            return queryResult.getSingleEntity(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
