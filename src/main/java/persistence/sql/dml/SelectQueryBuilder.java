package persistence.sql.dml;

import persistence.sql.ddl.TableClause;

public class SelectQueryBuilder {
    public static final String SELECT_ALL_QUERY = "SELECT * FROM %s";
    public static final String SELECT_BY_ID_QUERY = "SELECT * FROM %s WHERE %s = %d";
    private final TableClause tableClause;

    public SelectQueryBuilder(Class<?> entity) {
        this.tableClause = new TableClause(entity);
    }

    public String getFindAllQuery() {
        return String.format(SELECT_ALL_QUERY, tableClause.name());
    }
    public String getFindById(Long id) {
        return String.format(SELECT_BY_ID_QUERY, tableClause.name(), tableClause.primaryKeyName(), id);
    }
}
