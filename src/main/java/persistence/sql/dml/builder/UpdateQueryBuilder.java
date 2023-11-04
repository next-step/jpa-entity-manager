package persistence.sql.dml.builder;

import java.util.ArrayList;
import java.util.List;

public class UpdateQueryBuilder {
  private static final String UPDATE_SQL_QUERY = "UPDATE %s SET %s WHERE %s = %s;";
  private static final String UPDATE_SET_CLAUSE = "%s = %s";
  private static final String DELIMITER = ",";

  public String createUpdateQuery(String tableName, List<String> columns, List<String> values, String whereColumn, String whereValue) {
    List<String> setClause = new ArrayList<>();
    for (int i = 0 ; i<columns.size(); i++){
      setClause.add(String.format(UPDATE_SET_CLAUSE, columns.get(i), values.get(i)));
    }


    return String.format(UPDATE_SQL_QUERY, tableName, String.join(DELIMITER, setClause), whereColumn, whereValue);
  }

}
