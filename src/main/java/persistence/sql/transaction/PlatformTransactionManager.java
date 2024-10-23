package persistence.sql.transaction;

public interface PlatformTransactionManager {

    void startTransaction();

    void commit();

    void rollback();
}
