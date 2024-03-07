package persistence.sql.dml;

import persistence.sql.QueryException;
import persistence.sql.dialect.Dialect;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;
import persistence.sql.mapping.Value;
import util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.QueryBuilderConst.ENTER;
import static persistence.sql.QueryBuilderConst.SPACE;

public class DefaultDmlQueryBuilder implements DmlQueryBuilder {

    private final Dialect dialect;

    private final List<QueryValueBinder> queryValueBinders = initQueryValueBinders();

    public DefaultDmlQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    private List<QueryValueBinder> initQueryValueBinders() {
        return List.of(
                new QueryStringValueBinder(),
                new QueryNumberValueBinder()
        );
    }

    @Override
    public String buildInsertQuery(final Insert insert) {

        final Table table = insert.getTable();

        final StringBuilder statement = new StringBuilder()
                .append("insert")
                .append(ENTER)
                .append("into")
                .append(ENTER)
                .append(SPACE)
                .append(table.getName())
                .append(ENTER)
                .append(SPACE)
                .append("(");

        final String columnNameClause = buildInsertColumnsNameClause(insert.getInsertableColumnNames(dialect));

        statement.append(columnNameClause)
                .append(")")
                .append(ENTER)
                .append("values")
                .append(ENTER)
                .append(SPACE)
                .append("(");

        final String valuesClause = buildInsertColumnsValueClause(insert.getColumns(), insert.getInsertablePkColumns(dialect), dialect);

        return statement
                .append(valuesClause)
                .append(")")
                .toString();
    }

    private String buildInsertColumnsNameClause(final List<String> columnNames) {
        return String.join(", ", columnNames);
    }

    private String buildInsertColumnsValueClause(final List<Column> columns, final List<Column> pkColumns, final Dialect dialect) {
        return String.join(", ", getColumnsValueClause(columns), getPkColumnsValueClause(pkColumns, dialect));
    }

    private String getColumnsValueClause(final List<Column> columns) {
        return columns.stream()
                .map(this::getColumnValueClause)
                .collect(Collectors.joining(", "));
    }

    private String getColumnValueClause(final Column column) {
        final Value value = column.getValue();
        final QueryValueBinder queryValueBinder = findQueryValueBinder(value);

        return queryValueBinder.bind(value.getValue());
    }

    private String getPkColumnsValueClause(final List<Column> pkColumns, final Dialect dialect) {
        return pkColumns.stream()
                .map(column -> getPkValueClause(column, dialect))
                .collect(Collectors.joining(", "));
    }

    private String getPkValueClause(final Column column, final Dialect dialect) {
        if (column.isIdentifierKey() && dialect.getIdentityColumnSupport().hasIdentityInsertKeyword()) {
            return dialect.getIdentityColumnSupport().getIdentityInsertString();
        }

        return getColumnValueClause(column);
    }

    private QueryValueBinder findQueryValueBinder(final Value value) {
        return queryValueBinders.stream()
                .filter(binder -> binder.support(value))
                .findFirst()
                .orElseThrow(() -> new QueryException("not found InsertQueryValueBinder for " + value.getOriginalType() + " type"));
    }

    private String buildSelectColumnsClause(final List<Column> columns) {

        return columns.stream()
                .map(Column::getName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String buildSelectQuery(final Select select) {
        final Table table = select.getTable();

        return "select" +
                ENTER +
                SPACE +
                buildSelectColumnsClause(table.getColumns()) +
                ENTER +
                "from" +
                ENTER +
                SPACE +
                table.getName() +
                buildWhereClause(buildWheresClause(select.getWheres()));
    }

    @Override
    public String buildUpdateQuery(final Update update) {
        final Table table = update.getTable();

        return "update" +
                ENTER +
                SPACE +
                table.getName() +
                ENTER +
                "set" +
                ENTER +
                SPACE +
                buildUpdateSetClause(update.getColumns()) +
                buildWhereClause(buildWheresClause(update.getWheres()));
    }

    private String buildUpdateSetClause(final List<Column> columns) {
        return columns.stream()
                .map(column -> column.getName() + " = " + getColumnValueClause(column))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String buildDeleteQuery(final Delete delete) {
        final Table table = delete.getTable();

        return "delete" +
                ENTER +
                "from" +
                ENTER +
                SPACE +
                table.getName() +
                buildWhereClause(buildWheresClause(delete.getWheres()));
    }

    public String buildWheresClause(final List<Where> wheres) {
        return wheres.stream()
                .map(this::buildWhereClause)
                .collect(Collectors.joining(ENTER + SPACE));
    }

    public String buildWhereClause(final Where where) {
        final Value value = where.getColumnValue();
        final QueryValueBinder queryValueBinder = findQueryValueBinder(value);

        final String valueClause = queryValueBinder.bind(value.getValue());

        return (where.getLogicalOperator() + " " + where.getColumnName() + " " + where.getWhereOperator(valueClause)).trim();
    }

    private String buildWhereClause(final String whereClause) {
        final StringBuilder statement = new StringBuilder();
        if (StringUtils.isNotBlank(whereClause)) {
            statement.append(ENTER)
                    .append("where")
                    .append(ENTER)
                    .append(SPACE)
                    .append(whereClause);
        }

        return statement.toString();
    }

}
