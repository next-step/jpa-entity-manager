package persistence.sql.dml;

public class DMLQueryFormatter {

    private DMLQueryFormatter() {
    }

    public static String createInsertQuery(String tableName, String columnClause, String valueClause) {
        final String insertQueryFormat = "INSERT INTO %s (%s) VALUES (%s)";

        return java.lang.String.format(insertQueryFormat, tableName, columnClause, valueClause);
    }

    public static String createSelectQuery(String columnClause, String tableName) {
        final String selectQueryFormat = "SELECT %s FROM %s";

        return java.lang.String.format(selectQueryFormat, columnClause, tableName);
    }

    public static String createSelectByConditionQuery(String sql, String conditionClause) {
        String selectByConditionQueryFormat = "%s WHERE %s";

        return String.format(selectByConditionQueryFormat, sql, conditionClause);
    }

    public static String createDeleteQuery(String tableName, String conditionClause) {
        final String deleteQueryFormat = "DELETE FROM %s WHERE %s";

        return String.format(deleteQueryFormat, tableName, conditionClause);
    }

    public static String createUpdateQuery(String tableName, String columnValueSetClause, String conditionClause) {
        final String queryFormat = "UPDATE %s SET %s WHERE %s";

        return String.format(queryFormat, tableName, columnValueSetClause, conditionClause);
    }
}
