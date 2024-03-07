package persistence.sql.dml;

import jakarta.persistence.Entity;
import persistence.sql.ddl.TableClause;
import persistence.sql.ddl.value.ValueClauses;
import persistence.sql.exception.InvalidEntityException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.common.SqlConstant.COMMA;

public class UpdateQueryBuilder {
    public static final String UPDATE_QUERY = "UPDATE %s SET %s WHERE %s";
    public static final String UPDATE_QUERY_WITHOUT_WHERE = "UPDATE %s SET %s";
    public static final String EQUALS = "=";
    private final TableClause tableClause;

    public UpdateQueryBuilder(Class<?> entity) {
        if (!entity.isAnnotationPresent(Entity.class)) {
            throw new InvalidEntityException();
        }
        this.tableClause = new TableClause(entity);
    }

    public String getQuery(Object entity) {

        List<String> columnNames = this.tableClause.columnNames();

        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).collect(Collectors.toList());
        List<String> values = new ValueClauses(fields, entity).values();

        List<String> setClauses = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            setClauses.add(columnNames.get(i) + EQUALS + values.get(i));
        }

        if (tableClause.primaryKeyValue() != null) {
            String whereClause = String.format("WHERE %s = %d", tableClause.primaryKeyName(), tableClause.primaryKeyValue());
            return String.format(UPDATE_QUERY, tableClause.name(), String.join(COMMA, setClauses), whereClause);
        }

        return String.format(UPDATE_QUERY_WITHOUT_WHERE, tableClause.name(), String.join(COMMA, setClauses));
    }
}
