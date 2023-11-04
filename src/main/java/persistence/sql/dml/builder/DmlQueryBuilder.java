package persistence.sql.dml.builder;

public class DmlQueryBuilder {
  private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
  private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
  private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

  public DmlQueryBuilder() {
  }

  public String createUpdateQuery(String tableName, String targetColumn, String targetValue, String whereColumn, String whereValue){
    return updateQueryBuilder.createUpdateQuery(tableName, targetColumn, targetValue, whereColumn, whereValue);
  }

  public String createInsertQuery(String tableName, String columnClause, String valueClause){
    return insertQueryBuilder.createInsertQuery(tableName, columnClause, valueClause);
  }

  public String createDeleteQuery(String tableName, String targetName, Object targetValue){
    return deleteQueryBuilder.createDeleteQuery(tableName, targetName, targetValue);
  }

}
