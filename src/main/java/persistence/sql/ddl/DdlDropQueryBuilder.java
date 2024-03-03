package persistence.sql.ddl;

import persistence.sql.meta.simple.Table;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";

    public DdlDropQueryBuilder() {
    }

    public String dropDdl(Class<?> clazz) {
        final Table table = Table.ofClass(clazz);
        return String.format(DROP_TABLE_DEFAULT_DDL, table.name());
    }
}
