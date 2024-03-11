package persistence.sql.dml;

import persistence.sql.ddl.table.Table;
import persistence.sql.dml.clause.ValueClause;
import persistence.sql.dml.clause.WhereClause;

public class DMLGenerator {

    private static final String INSERT_QUERY = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String FIND_QUERY = "SELECT * FROM %s%s;";
    private static final String UPDATE_QUERY = "UPDATE %s SET %s WHERE id = %d;";
    private static final String DELETE_QUERY = "DELETE FROM %s%s;";

    private final Table table;

    public DMLGenerator(Table table) {
        this.table = table;
    }

    public String generateInsert(Object entity) {
        ValueClause valueClause = new ValueClause(entity);

        return String.format(INSERT_QUERY, table.getName(), table.getColumnsClause(), valueClause.getValueClause());
    }

    public String generateFindAll() {
        return String.format(FIND_QUERY, table.getName(), "");
    }

    public String generateFindById(Long id) {
        String whereClause = String.format(" where id = %d", id);

        return String.format(FIND_QUERY, table.getName(), whereClause);
    }

    public String generateUpdateById(Object entity, Long id) {
        WhereClause whereClause = new WhereClause(entity);

        return String.format(UPDATE_QUERY, table.getName(), whereClause.getWhereClause(", "), id);
    }

    public String generateDelete(Object entity) {
        WhereClause whereClause = new WhereClause(entity);

        return String.format(DELETE_QUERY, table.getName(), " where " + whereClause.getWhereClause(" AND "));
    }
}
