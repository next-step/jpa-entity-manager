package persistence.sql.dml;

import jakarta.persistence.Entity;
import persistence.sql.ddl.TableClause;
import persistence.sql.ddl.value.ValueClauses;
import persistence.sql.exception.InvalidEntityException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

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

        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).toList();
        List<String> values = new ValueClauses(fields, entity).values();

        StringBuilder setClauses = new StringBuilder();
        for (int i = 0; i < columnNames.size(); i++) {
            setClauses.append(columnNames.get(i)).append(EQUALS).append(values.get(i));
        }

        if (tableClause.primaryKeyValue() != null) {
            String whereClause = String.format("WHERE %s = %d", tableClause.primaryKeyName(), tableClause.primaryKeyValue());
            return String.format(UPDATE_QUERY, tableClause.name(), setClauses, whereClause);
        }

        return String.format(UPDATE_QUERY_WITHOUT_WHERE, tableClause.name(), setClauses);
    }
}
