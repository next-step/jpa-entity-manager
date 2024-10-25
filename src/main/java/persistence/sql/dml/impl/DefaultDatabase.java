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
    private Connection connection;

    public DefaultDatabase(DatabaseServer dataSource) {
        this.server = dataSource;
    }

    @Override
    public Connection getConnection() {
        try {

            if(connection == null || connection.isClosed()) {
                connection = server.getConnection();
            }

            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object executeUpdate(String query) {
        try {
            Statement statement = getConnection().createStatement();
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
        } finally {
            closeConsiderConnection();
        }
    }

    @Override
    public <T> T executeQuery(String query, RowMapper<T> rowMapper) {
        try {
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            return rowMapper.mapRow(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to execute query: " + query, e);
        } finally {
            closeConsiderConnection();
        }
    }

    private void closeConsiderConnection() {
        try {
            if (connection != null && connection.getAutoCommit()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
