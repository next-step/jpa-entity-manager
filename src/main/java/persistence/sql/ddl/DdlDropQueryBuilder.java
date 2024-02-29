package persistence.sql.ddl;

import persistence.sql.meta.TableName;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";
    private final TableName tableName;

    public DdlDropQueryBuilder(final TableName tableName) {
        this.tableName = tableName;
    }

    public String dropDdl() {
        return String.format(DROP_TABLE_DEFAULT_DDL, this.tableName.name());
    }
}
