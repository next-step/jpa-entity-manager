package persistence.sql.ddl.h2;

import persistence.dialect.Database;
import persistence.sql.ddl.CreateDdlBuilder;

public class H2CreateDdlBuilder extends CreateDdlBuilder {

    public H2CreateDdlBuilder() {
        super(Database.H2.createDialect());
    }
}
