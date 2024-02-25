package persistence.sql.ddl;

import database.DatabaseServer;
import database.H2;

public class DDLQueryBuilderFactory {

    public static DDLQueryBuilder getDDLQueryBuilder(DatabaseServer server) {
        if (server instanceof H2) {
            return new H2DDLQueryBuilder();
        } else {
            return null;
        }
    }
}
