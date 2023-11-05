package persistence.sql.dml;

import persistence.sql.SQLEscaper;
import persistence.sql.TableSQLMapper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FindByIdQueryBuilder {
    public String generateSQLQuery(String tableName, String[] columnNames, String primaryKeyName, Object primaryKeyValue) {
        return "SELECT " +
            Arrays
                .stream(columnNames)
                .map(SQLEscaper::escapeNameByBacktick)
                .collect(Collectors.joining(", ")) + ", " + SQLEscaper.escapeNameByBacktick(primaryKeyName) +
            " FROM " +
            SQLEscaper.escapeNameByBacktick(tableName) +
            " WHERE " +
            SQLEscaper.escapeNameByBacktick(primaryKeyName) + " = " + TableSQLMapper.changeColumnValueToString(primaryKeyValue) +
            ";";
    }
}
