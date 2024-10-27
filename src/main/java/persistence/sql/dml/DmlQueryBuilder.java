package persistence.sql.dml;

import persistence.model.EntityColumn;
import persistence.model.EntityFactory;
import persistence.model.EntityTable;
import persistence.sql.dml.clause.FindOption;
import persistence.sql.dml.clause.FindOptionBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.clause.EqualClause;

import java.util.*;
import java.util.stream.Collectors;

public class DmlQueryBuilder {
    private final Dialect dialect;
    private static final String INSERT_FORMAT = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String DELETE_FORMAT = "DELETE FROM %s WHERE %s";
    private static final String SELECT_FORMAT = "SELECT %s FROM %s";
    private static final String SELECT_ALL = "*";
    private static final String UPDATE_FORMAT = "UPDATE %s SET %s WHERE %s";

    public DmlQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public String appendSemicolon(String query) {
        return query + ";";
    }

    public String buildUpdateQuery(
            String tableName,
            List<Map.Entry<String, Object>> updatingColumns,
            Map.Entry<String, Object> where
    ) {
        String setColumnSql = updatingColumns.stream()
                .map(column -> new EqualClause(column.getKey(), column.getValue()).toSql(dialect))
                .collect(Collectors.joining(", "));

        String whereSql = new EqualClause(where.getKey(), where.getValue()).toSql(dialect);

        String sql = String.format(UPDATE_FORMAT,
                dialect.getIdentifierQuoted(tableName),
                setColumnSql,
                whereSql
        );
        return appendSemicolon(sql);
    }

    public String buildInsertQuery(String tableName, List<Map.Entry<String, Object>> insertingColumns) {
        List<String> insertingColumnNames = insertingColumns.stream()
                .map(Map.Entry::getKey)
                .toList();

        List<Object> insertingColumnValues = insertingColumns.stream()
                .map(Map.Entry::getValue)
                .toList();

        String sql =  String.format(
                INSERT_FORMAT,
                dialect.getIdentifierQuoted(tableName),
                dialect.getIdentifiersQuoted(insertingColumnNames),
                dialect.getValuesQuoted(insertingColumnValues)
        );
        return appendSemicolon(sql);
    }

    public String buildDeleteQuery(String tableName, Map.Entry<String, Object> where) {
        String whereSql = new EqualClause(where.getKey(), where.getValue()).toSql(dialect);

        String deleteSql = String.format(
                DELETE_FORMAT,
                dialect.getIdentifierQuoted(tableName),
                whereSql
        );
        return appendSemicolon(deleteSql);
    }

    public String buildSelectByIdQuery(Class<?> entityClass, Object id) {
        EntityTable table = EntityFactory.createEmptySchema(entityClass);

        EntityColumn conditionColumn = table.getColumn("id");
        FindOption findOption = new FindOptionBuilder()
                .where(new EqualClause(conditionColumn.getName(), id))
                .build();

        return buildSelectQuery(entityClass, findOption);
    }

    private String buildSelectQuery(Class<?> entityClass, FindOption findOption) {
        EntityTable table = EntityFactory.createEmptySchema(entityClass);

        List<String> selectingColumnNames = findOption.getSelectingColumns().stream()
                .map(EntityColumn::getName)
                .toList();

        String selectingColumnNamesJoined = selectingColumnNames.isEmpty()
                ? SELECT_ALL
                : dialect.getIdentifiersQuoted(selectingColumnNames);

        String query = String.format(
                SELECT_FORMAT,
                selectingColumnNamesJoined,
                dialect.getIdentifierQuoted(table.getName()));

        if (!findOption.getWhere().isEmpty()) {
            return query + " " + findOption.joinWhereClauses(dialect) + ";";
        }
        return appendSemicolon(query);
    }
}
