package persistence.sql.dml;

import persistence.dialect.Dialect;

import java.util.List;
import java.util.stream.Collectors;

public class DmlGenerator {

    private final Dialect dialect;

    public DmlGenerator(final Dialect dialect) {
        this.dialect = dialect;
    }

    public String insert(final String tableName, final List<String> columnNames, final List<Object> values) {
        return InsertQueryBuilder.builder()
                .table(tableName)
                .addData(columnNames, convertToStrings(values))
                .build();
    }

    public String findAll(final String tableName, final List<String> columnNames) {
        return new SelectQueryBuilder(dialect)
                .table(tableName)
                .column(columnNames)
                .build();
    }

    public String findById(final String tableName, final List<String> columnNames, final String idColumnName, final Object id) {
        return new SelectQueryBuilder(dialect)
                .table(tableName)
                .column(columnNames)
                .where(idColumnName, String.valueOf(id))
                .build();
    }

    public String delete(final String tableName, final String idColumnName, final Object id) {
        return DeleteQueryBuilder.builder()
                .table(tableName)
                .where(idColumnName, convertToString(id))
                .build();
    }

    public String update(final String tableName, final List<String> columnNames, final List<Object> values, final String idColumnName, final Object idValue) {
        return UpdateQueryBuilder.builder()
                .table(tableName)
                .addData(columnNames, convertToStrings(values))
                .where(idColumnName, convertToString(idValue))
                .build();
    }

    private String convertToString(final Object object) {
        return object instanceof String ?
                addSingleQuote(object) : String.valueOf(object);
    }

    private List<String> convertToStrings(final List<Object> data) {
        return data.stream()
                .map(this::convertToString)
                .collect(Collectors.toList());
    }

    private String addSingleQuote(final Object value) {
        return "'" + value + "'";
    }
}
