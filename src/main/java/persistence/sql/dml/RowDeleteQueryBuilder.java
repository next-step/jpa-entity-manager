package persistence.sql.dml;

import persistence.sql.SQLEscaper;
import persistence.sql.TableSQLMapper;

public class RowDeleteQueryBuilder {

    public String generateSQLQuery(String tableName, String primaryKeyName, Object primaryKeyValue) {

        return "DELETE FROM " +
            SQLEscaper.escapeNameByBacktick(tableName) +
            " WHERE " +
            SQLEscaper.escapeNameByBacktick(primaryKeyName) + " = " + TableSQLMapper.changeColumnValueToString(primaryKeyValue) +
            ";";
    }
}
