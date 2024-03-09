package persistence.sql.ddl;

public class DropQueryBuilder {
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS %s";
    private final TableClause tableClause;

    public DropQueryBuilder(Class<?> entity) {
        this.tableClause = new TableClause(entity);
    }
    public String getQuery() {
        return String.format(DROP_TABLE, tableClause.name());
    }
}
