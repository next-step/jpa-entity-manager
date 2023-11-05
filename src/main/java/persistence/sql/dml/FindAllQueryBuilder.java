package persistence.sql.dml;

import persistence.sql.SQLEscaper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FindAllQueryBuilder {

    public String generateSQLQuery(String tableName, String[] columnNames) {
        return "SELECT " +
            Arrays
                .stream(columnNames)
                .map(SQLEscaper::escapeNameByBacktick)
                .collect(Collectors.joining(",")) +
            " FROM " +
            SQLEscaper.escapeNameByBacktick(tableName) +
            ";";
    }
}
