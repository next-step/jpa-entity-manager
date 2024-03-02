package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.mapping.Column;
import persistence.sql.mapping.Table;
import util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.QueryBuilderConst.ENTER;
import static persistence.sql.QueryBuilderConst.SPACE;

public class DefaultDmlQueryBuilder implements DmlQueryBuilder {

    private final Dialect dialect;

    public DefaultDmlQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
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

        final String columnNameClause = insert.columnNameClause(dialect);

        statement.append(columnNameClause)
                .append(")")
                .append(ENTER)
                .append("values")
                .append(ENTER)
                .append(SPACE)
                .append("(");

        final String valuesClause = insert.columnValueClause(dialect);

        return statement
                .append(valuesClause)
                .append(")")
                .toString();
    }

    private String insertColumnsClause(final List<Column> columns) {

        return columns.stream()
                .map(this::columnClause)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
    }

    private String columnClause(final Column column) {

        return column.getName();
    }

    @Override
    public String buildSelectQuery(final Select select) {
        final Table table = select.getTable();

        return "select" +
                ENTER +
                SPACE +
                insertColumnsClause(table.getColumns()) +
                ENTER +
                "from" +
                ENTER +
                SPACE +
                table.getName() +
                buildWhereClause(select.getWhereClause());
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
                update.columnSetClause() +
                buildWhereClause(update.getWhereClause());
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
                buildWhereClause(delete.getWhereClause());
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
