package persistence.sql.dml;

import persistence.sql.SQLEscaper;
import persistence.sql.TableSQLMapper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RowInsertQueryBuilder {

    public static String generateSQLQuery(String tableName, String[] columns, Object[] values, String primaryKeyName, Object primaryKeyValue) {
        String primaryKeyColumName = "";
        String primaryKeyColumnValue = "";
        if (primaryKeyValue != null) {
            primaryKeyColumName = primaryKeyName + ", ";
            primaryKeyColumnValue = primaryKeyValue + ", ";
        }

        return "INSERT INTO " +
            SQLEscaper.escapeNameByBacktick(tableName) +
            " (" +
            primaryKeyColumName +
            Arrays
                .stream(columns)
                .map(SQLEscaper::escapeNameByBacktick)
                .collect(Collectors.joining(", ")) +
            ") VALUES (" +
            primaryKeyColumnValue +
            Arrays
                .stream(values)
                .map(TableSQLMapper::changeColumnValueToString)
                .collect(Collectors.joining(", ")) +
            ");";
    }
}
