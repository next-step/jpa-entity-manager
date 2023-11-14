package persistence.sql.dml.builder;

import java.util.List;

public class SelectQueryBuilder {
  private static final String SELECT_SQL_QUERY = "SELECT %s FROM %s;";
  private static final String SELECT_WHERE_SQL_QUERY = "SELECT %s FROM %s WHERE %s=%s;";
  private static final String SELECT_WHERE_IN_SQL_QUERY = "SELECT %s FROM %s WHERE %s";
  private static final String SELECT_WHERE_CLAUSE = "%s IN (%s)";
  private static final String DELIMITER = ",";
  public String createSelectQuery(String columnClause, String tableName) {

    return String.format(SELECT_SQL_QUERY, columnClause, tableName);
  }

  public String createSelectByFieldQuery(String columnClause, String tableName, String targetName, Object targetValue) {

    return String.format(SELECT_WHERE_SQL_QUERY, columnClause, tableName, targetName, targetValue);
  }

  public String createSelectByFieldsQuery(String columnClause, String tableName, String targetName, List<String> targetValue) {
    String ids = String.join(DELIMITER, targetValue);
    String whereClause = String.format(SELECT_WHERE_CLAUSE, targetName, ids);
    return String.format(SELECT_WHERE_IN_SQL_QUERY, columnClause, tableName, whereClause);
  }

}
