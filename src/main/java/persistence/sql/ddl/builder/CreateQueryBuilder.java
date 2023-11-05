package persistence.sql.ddl.builder;

public class CreateQueryBuilder {
  private static final String CREATE_SQL_QUERY = "CREATE TABLE %s (%s);";

  public CreateQueryBuilder() {
  }

  public String createCreateQuery(String tableName, String columns) {
    return String.format(CREATE_SQL_QUERY, tableName, columns);
  }

}
