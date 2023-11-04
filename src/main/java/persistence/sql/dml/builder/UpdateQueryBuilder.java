package persistence.sql.dml.builder;

public class UpdateQueryBuilder {
  private static final String UPDATE_SQL_QUERY = "UPDATE %s SET %s = %s WHERE %s = %s;";

  public String createUpdateQuery(String tableName, String targetColumn, String targetValue, String whereColumn, String whereValue) {
    return String.format(UPDATE_SQL_QUERY, tableName, targetColumn, targetValue, whereColumn, whereValue);
  }

}
