package persistence.sql.dml.impl;

import database.DatabaseServer;
import jdbc.RowMapper;
import persistence.sql.dml.Database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DefaultDatabase implements Database {
    private final DatabaseServer server;

    public DefaultDatabase(DatabaseServer dataSource) {
        this.server = dataSource;
    }

    @Override
    public Object executeUpdate(String query) {
        try (Connection connection = server.getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getObject(1);
                }

                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute update: " + query, e);
        }
    }

    @Override
    public <T> T executeQuery(String query, RowMapper<T> rowMapper) {
        try (Connection connection = server.getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            return rowMapper.mapRow(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute query: " + query, e);
        }
    }
}
