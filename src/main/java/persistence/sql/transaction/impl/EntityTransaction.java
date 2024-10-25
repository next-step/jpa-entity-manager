package persistence.sql.transaction.impl;

import persistence.sql.event.FlushEventListener;
import persistence.sql.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;

public class EntityTransaction implements Transaction {
    private final FlushEventListener flushEventListener;
    private boolean active;
    private Connection connection;

    public EntityTransaction(FlushEventListener flushEventListener) {
        active = false;
        this.flushEventListener = flushEventListener;
    }

    public EntityTransaction(Connection connection, FlushEventListener flushEventListener) {
        this.active = false;
        this.connection = connection;
        this.flushEventListener = flushEventListener;
    }

    @Override
    public void begin() {
        if (active) {
            throw new IllegalStateException("Transaction is already active");
        }

        try {
            active = true;
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void commit() {
        if (!active) {
            throw new IllegalStateException("Transaction is not active");
        }

        try {
            flushEventListener.onFlush();
            connection.commit();
            active = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
    }

    @Override
    public void rollback() {
        if (!active) {
            throw new IllegalStateException("Transaction is not active");
        }

        try {
            connection.rollback();
            active = false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            cleanup();
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void connect(Connection connection) {
        this.connection = connection;
    }
}
