package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.Table;

import java.util.stream.Collectors;

public class InsertQueryBuilder {

    public static final String INSERT_DEFAULT_DML = "insert into %s (%s) values (%s)";
    public static final String COMMA = ", ";

    public InsertQueryBuilder() {
    }

    public String createInsertQuery(Table table) {
        return String.format(INSERT_DEFAULT_DML, table.name(), insertColumns(table.columns()),
                insertValues(table.columns()));
    }

    private String insertColumns(Columns columns) {
        return String.join(COMMA, columns.names());
    }

    private String insertValues(Columns columns) {
        return columns.values().stream()
                .collect(Collectors.joining(COMMA));
    }
}
