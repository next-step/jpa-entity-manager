package persistence.sql.ddl;

import persistence.dialect.Database;

public class H2CreateDdlBuilder extends CreateDdlBuilder {

    public H2CreateDdlBuilder() {
        super(Database.H2.createDialect());
    }
}
