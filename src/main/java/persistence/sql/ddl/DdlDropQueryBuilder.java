package persistence.sql.ddl;

import persistence.sql.meta.simple.SimpleEntityMetaCreator;
import persistence.sql.meta.simple.SimpleTable;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";

    public DdlDropQueryBuilder() {
    }

    public String dropDdl(Class<?> clazz) {
        final SimpleTable table = SimpleEntityMetaCreator.tableOfClass(clazz);
        return String.format(DROP_TABLE_DEFAULT_DDL, table.name());
    }
}
