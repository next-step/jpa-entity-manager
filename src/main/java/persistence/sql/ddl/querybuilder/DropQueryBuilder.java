package persistence.sql.ddl.querybuilder;

import persistence.sql.ddl.clause.table.TableClause;

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
