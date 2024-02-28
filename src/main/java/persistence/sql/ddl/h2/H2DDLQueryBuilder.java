package persistence.sql.ddl.h2;

import persistence.sql.ddl.DDLQueryBuilder;

public class H2DDLQueryBuilder extends DDLQueryBuilder {

    public H2DDLQueryBuilder() {
        super(new H2DDLSqlGenerator());
    }

}
