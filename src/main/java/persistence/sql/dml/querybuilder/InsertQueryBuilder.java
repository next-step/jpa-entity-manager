package persistence.sql.dml.querybuilder;

import persistence.sql.ddl.clause.table.TableClause;
import persistence.sql.dml.clause.value.ValueClauses;
import persistence.sql.exception.InvalidValueClausesException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static persistence.sql.common.SqlConstant.CLOSING_PARENTHESIS;
import static persistence.sql.common.SqlConstant.COMMA;

public class InsertQueryBuilder {
    public static final String INSERT_QUERY_START = "INSERT INTO %s (";
    public static final String VALUES = " VALUES (";
    private final TableClause tableClause;

    public InsertQueryBuilder(Class<?> clazz) {
        this.tableClause = new TableClause(clazz);
    }

    public String getInsertQuery(Object entity) {
        return String.format(INSERT_QUERY_START, tableClause.name()) +
                String.join(COMMA, tableClause.columnNames()) +
                CLOSING_PARENTHESIS +
                VALUES +
                String.join(COMMA, new ValueClauses(entity).getQueries()) +
                CLOSING_PARENTHESIS;
    }

    public String getInsertQuery(List<String> columnNames, List<Object> columValues) {

        if (columnNames.size() != columValues.size()) {
            throw new InvalidValueClausesException();
        }

        Object entity;
        try {
            entity = initInstance(columnNames, columValues);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 InstantiationException e) {
            throw new IllegalArgumentException("잚못된 요청입니다.");

        }
        return this.getInsertQuery(entity);
    }

    private Object initInstance(List<String> columnNames, List<Object> columValues)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            NoSuchFieldException {
        Object entity = tableClause.newInstance();

        for (int i = 0; i < columnNames.size(); i++) {
            Field field = entity.getClass().getDeclaredField(columnNames.get(i));
            field.setAccessible(true);
            field.set(entity, columValues.get(i));
        }
        return entity;
    }
}
