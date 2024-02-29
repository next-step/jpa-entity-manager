package persistence.sql.ddl;

public class DdlDropQueryBuilder {

    private static final String DROP_TABLE_DEFAULT_DDL = "drop table if exists %s CASCADE";
    private final String tableName;

    public DdlDropQueryBuilder(final String tableName) {
        this.tableName = tableName;
    }

    public String dropDdl() {
        return String.format(DROP_TABLE_DEFAULT_DDL, this.tableName);
    }
}
