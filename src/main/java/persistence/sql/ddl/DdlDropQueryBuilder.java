package persistence.sql.ddl;

import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.Table;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";
    private final EntityMetaCreator entityMetaCreator;

    public DdlDropQueryBuilder(final EntityMetaCreator entityMetaCreator) {
        this.entityMetaCreator = entityMetaCreator;
    }

    public String dropDdl(Class<?> clazz) {
        final Table table = entityMetaCreator.createByClass(clazz);
        return String.format(DROP_TABLE_DEFAULT_DDL, table.name());
    }
}
