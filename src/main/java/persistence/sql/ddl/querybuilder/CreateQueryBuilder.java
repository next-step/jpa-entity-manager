package persistence.sql.ddl.querybuilder;

import persistence.sql.ddl.clause.table.TableClause;

import static persistence.sql.common.SqlConstant.*;

public class CreateQueryBuilder {
    public static final String CREATE_TABLE_START = "CREATE TABLE IF NOT EXISTS %s ";
    private final TableClause tableClause;

    public CreateQueryBuilder(Class<?> clazz) {
        this.tableClause = new TableClause(clazz);
    }

    public String getQuery() {
        return String.format(CREATE_TABLE_START, tableClause.name()) +
                OPENING_PARENTHESIS +
                getIdQuery() +
                getColumnQuery() +
                CLOSING_PARENTHESIS;
    }

    private String getIdQuery() {
        return tableClause.createIdQuery() + COMMA;
    }

    private String getColumnQuery() {
        return String.join(COMMA, tableClause.columnQueries());
    }
}
