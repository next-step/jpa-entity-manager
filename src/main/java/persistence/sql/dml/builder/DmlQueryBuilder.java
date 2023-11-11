package persistence.sql.dml.builder;

import java.util.List;

public class DmlQueryBuilder {
  private final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
  private final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
  private final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
  public DmlQueryBuilder() {
  }

  public String createUpdateQuery(String tableName, List<String> columns, List<String> values, String whereColumn, String whereValue){
    return updateQueryBuilder.createUpdateQuery(tableName, columns, values, whereColumn, whereValue);
  }

  public String createInsertQuery(String tableName, String columnClause, String valueClause){
    return insertQueryBuilder.createInsertQuery(tableName, columnClause, valueClause);
  }

  public String createDeleteQuery(String tableName, String targetName, Object targetValue){
    return deleteQueryBuilder.createDeleteQuery(tableName, targetName, targetValue);
  }
}
