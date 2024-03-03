package persistence.sql.ddl;

import database.DatabaseVendor;
import persistence.sql.ddl.h2.H2DDLQueryBuilder;

public class DDLQueryBuilderFactory {

    public static DDLQueryBuilder getDDLQueryBuilder(DatabaseVendor databaseVendor) {
        if (databaseVendor == DatabaseVendor.H2) {

            return new H2DDLQueryBuilder();
        } else {

            return null;
        }
    }
}
