package persistence.sql.transaction;

public interface TransactionManager {

    void startTransaction();

    void commit();

    void rollback();
}
