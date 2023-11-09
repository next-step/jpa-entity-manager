package persistence.sql.dml;

import persistence.sql.SQLEscaper;
import persistence.sql.TableSQLMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class RowUpdateQueryBuilder {
    public static String generateSQLQuery(String tableName, String[] columnNames, Object[] values, String primaryKeyName, Object primaryKeyValue) {
        ArrayList<String> columnAndValues = new ArrayList<>();
        Iterator<String> nameIterator = Arrays.stream(columnNames).iterator();
        Iterator<Object> valueIterator = Arrays.stream(values).iterator();
        while (nameIterator.hasNext() && valueIterator.hasNext()) {
            columnAndValues.add(nameIterator.next() + " = " + TableSQLMapper.changeColumnValueToString(valueIterator.next()));
        }

        return "UPDATE " + SQLEscaper.escapeNameByBacktick(tableName) +
            " SET " + String.join(", ", columnAndValues) +
            " WHERE " + primaryKeyName + " = " + primaryKeyValue;
    }
}
