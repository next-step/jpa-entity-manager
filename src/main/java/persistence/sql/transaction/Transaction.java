package persistence.sql.transaction;

import java.sql.Connection;

public interface Transaction {

    void begin();

    void commit();

    void rollback();

    boolean isActive();

    void cleanup();

    void connect(Connection connection);
}
