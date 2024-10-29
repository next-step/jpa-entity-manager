package jdbc;

import java.sql.Connection;
import persistence.entity.EntityManager;

public class TransactionalJdbcTemplate extends JdbcTemplate{

    private EntityManager entityManager;

    public TransactionalJdbcTemplate(Connection connection) {
        super(connection);}

    public void beginTransaction() {
        try {
            getConnection().setAutoCommit(false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start transaction", e);
        }
    }

    public void commit() {
        try {
            entityManager.flush();
            getConnection().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to commit transaction", e);
        } finally {
            setAutoCommitTrue();
        }
    }

    public void rollback() {
        try {
            getConnection().rollback();
        } catch (Exception e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        } finally {
            setAutoCommitTrue();
        }
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private void setAutoCommitTrue() {
        try {
            getConnection().setAutoCommit(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore auto-commit mode", e);
        }
    }

}
