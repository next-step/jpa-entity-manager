package persistence.sql.dml;

import jakarta.persistence.Entity;
import persistence.sql.ddl.TableClause;
import persistence.sql.ddl.value.ValueClauses;
import persistence.sql.exception.InvalidEntityException;
import persistence.sql.exception.InvalidValueClausesException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.common.SqlConstant.CLOSING_PARENTHESIS;
import static persistence.sql.common.SqlConstant.COMMA;

public class InsertQueryBuilder {
    public static final String INSERT_QUERY_START = "INSERT INTO %s (";
    public static final String VALUES = " VALUES (";
    private final TableClause tableClause;

    public InsertQueryBuilder(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new InvalidEntityException();
        }
        this.tableClause = new TableClause(entity);
    }

    public String getInsertQuery(Object entity) {

        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).collect(Collectors.toList());

        return String.format(INSERT_QUERY_START, tableClause.name()) +
                String.join(COMMA, tableClause.columnNames()) +
                CLOSING_PARENTHESIS +
                VALUES +
                String.join(COMMA, new ValueClauses(fields, entity).getQueries()) +
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
