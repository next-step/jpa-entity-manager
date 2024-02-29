package persistence.sql.dml;

import persistence.sql.meta.Columns;
import persistence.sql.meta.TableName;

public class InsertQueryBuilder {

    public static final String INSERT_DEFAULT_DML = "insert into %s (%s) values (%s)";
    public static final String COMMA = ", ";
    private final TableName tableName;
    private final Columns columns;

    public InsertQueryBuilder(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public String createInsertQuery(Object object) {
        return String.format(INSERT_DEFAULT_DML, tableName(), insertColumns(), insertValues(object));
    }

    private String tableName() {
        return tableName.name();
    }

    private String insertColumns() {
        return String.join(COMMA, this.columns.names());
    }

    private String insertValues(Object object) {
        return String.join(COMMA, this.columns.values(object));
    }
}
