package persistence.sql.dml;

import persistence.sql.ddl.TableClause;

public class DeleteQueryBuilder {
    public static final String DELETE_ALL_QUERY = "DELETE FROM %s";
    public static final String DELETE_BY_ID_QUERY = "DELETE FROM %s where %s = %d";
    private final TableClause tableClause;

    public DeleteQueryBuilder(Class<?> entity) {
        this.tableClause = new TableClause(entity);
    }

    public String deleteAll() {
        return String.format(DELETE_ALL_QUERY, tableClause.name());
    }

    public String deleteById(Long id) {
        return String.format(DELETE_BY_ID_QUERY, tableClause.name(), tableClause.primaryKeyName(), id);
    }
}
