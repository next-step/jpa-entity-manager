package persistence.entity;

import java.sql.SQLException;

public interface EntityManagerFactory {
    EntityManger createEntityManager() throws SQLException;
}
