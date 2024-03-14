package persistence.sql.ddl;

public class DropQueryBuilder {
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    private final TableClause tableClause;

    public DropQueryBuilder(Class<?> clazz) {
        this.tableClause = new TableClause(clazz);
    }
    public String getQuery() {
        return String.format(DROP_TABLE, tableClause.name());
    }
}
