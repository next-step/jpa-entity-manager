package persistence.sql.dml;

import persistence.sql.column.Columns;
import persistence.sql.column.IdColumn;
import persistence.sql.column.TableColumn;
import persistence.sql.dialect.Dialect;

import java.util.stream.Collectors;


public class UpdateQueryBuilder implements DmlQueryBuilder {
    private static final String UPDATE_QUERY_FORMAT = "update %s set %s ";
    private static final String COLUMN_FORMAT = "%s = %s";
    private static final String WHERE_CLAUSE_FORMAT = "where %s = %d";
    private static final String COMMA = ", ";

    private final Dialect dialect;
    private Columns columns;
    private IdColumn idColumn;
    private String query;

    public UpdateQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public UpdateQueryBuilder build(Object entity) {
        Class<?> clazz = entity.getClass();
        TableColumn tableColumn = new TableColumn(clazz);
        this.columns = new Columns(entity, dialect);
        this.idColumn = new IdColumn(entity, dialect);
        this.query = String.format(UPDATE_QUERY_FORMAT, tableColumn.getName(), getColumnFormat(columns));
        return this;
    }

    private String getColumnFormat(Columns columns) {
        return columns.getValues().stream()
                .map(column -> String.format(COLUMN_FORMAT, column.getName(), column.getValue()))
                .collect(Collectors.joining(COMMA));
    }

    @Override
    public String toStatementWithId(Object id) {
        return query + String.format(WHERE_CLAUSE_FORMAT, idColumn.getName(), id);
    }
}
