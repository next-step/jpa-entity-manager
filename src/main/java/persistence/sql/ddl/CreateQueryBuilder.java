package persistence.sql.ddl;

import static persistence.sql.common.SqlConstant.*;

public class CreateQueryBuilder {
    public static final String CREATE_TABLE_START = "CREATE TABLE IF NOT EXISTS %s ";
    private final TableClause tableClause;

    public CreateQueryBuilder(Class<?> entity) {
        this.tableClause = new TableClause(entity);
    }

    public String getQuery() {
        return String.format(CREATE_TABLE_START, tableClause.name()) +
                OPENING_PARENTHESIS +
                getIdQuery() +
                getColumnQuery() +
                CLOSING_PARENTHESIS;
    }

    private String getIdQuery() {
        return tableClause.createQuery() + COMMA;
    }

    private String getColumnQuery() {
        return String.join(COMMA, tableClause.columnQueries());
    }
}
