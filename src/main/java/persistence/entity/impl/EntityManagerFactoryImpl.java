package persistence.entity.impl;


import database.DatabaseServer;
import java.sql.SQLException;
import persistence.entity.EntityManagerFactory;
import persistence.entity.EntityManger;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final DatabaseServer databaseServer;

    public EntityManagerFactoryImpl(DatabaseServer databaseServer) {
        this.databaseServer = databaseServer;
    }

    @Override
    public EntityManger createEntityManager() throws SQLException {
        return new EntityManagerImpl(databaseServer.getConnection());
    }

}
