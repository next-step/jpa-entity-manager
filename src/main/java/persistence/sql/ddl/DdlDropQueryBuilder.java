package persistence.sql.ddl;

import persistence.sql.meta.Table;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";

    public DdlDropQueryBuilder() {
    }

    public String dropDdl(Table table) {
        return String.format(DROP_TABLE_DEFAULT_DDL, table.name());
    }
}
