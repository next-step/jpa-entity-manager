package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;
import persistence.sql.meta.simple.SimpleTable;

import java.util.stream.Collectors;

public class InsertQueryBuilder {

    public static final String INSERT_DEFAULT_DML = "insert into %s (%s) values (%s)";
    public static final String COMMA = ", ";

    public InsertQueryBuilder() {
    }

    public String createInsertQuery(Object object) {
        final SimpleTable table = SimpleEntityMetaCreator.tableOfInstance(object);

        return String.format(INSERT_DEFAULT_DML, table.name(), insertColumns(table.columns()),
                insertValues(table.columns()));
    }

    private String insertColumns(Columns columns) {
        return String.join(COMMA, columns.names());
    }

    private String insertValues(Columns columns) {
//        return String.join(COMMA, String.valueOf(columns.values()));
        return columns.values().stream()
                .collect(Collectors.joining(COMMA));
    }
}
