package persistence.entity;

import java.sql.SQLException;

public interface EntityManagerFactory {
    EntityManager createEntityManager() throws SQLException;
}
