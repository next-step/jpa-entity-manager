package persistence.sql.dml.builder;

public class InsertQueryBuilder {

  private static final String INSERT_SQL_QUERY = "INSERT INTO %s (%s) values (%s);";

  public String createInsertQuery(String tableName, String columnClause, String valueClause) {
    return String.format(INSERT_SQL_QUERY, tableName, columnClause, valueClause);
  }


}
