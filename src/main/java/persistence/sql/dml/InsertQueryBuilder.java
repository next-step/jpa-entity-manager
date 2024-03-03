package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.simple.Table;

public class InsertQueryBuilder {

    public static final String INSERT_DEFAULT_DML = "insert into %s (%s) values (%s)";
    public static final String COMMA = ", ";

    public InsertQueryBuilder() {
    }

    public String createInsertQuery(Object object) {
        final Table table = Table.ofInstance(object);

        return String.format(INSERT_DEFAULT_DML, table.name(), insertColumns(table.columns()),
                insertValues(table.columns(), object));
    }

    private String insertColumns(Columns columns) {
        return String.join(COMMA, columns.names());
    }

    private String insertValues(Columns columns, Object object) {
        return String.join(COMMA, columns.values(object));
    }
}
