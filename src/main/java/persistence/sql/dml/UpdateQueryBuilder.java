package persistence.sql.dml;

import persistence.sql.ddl.TableClause;
import persistence.sql.ddl.value.ValueClauses;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.common.SqlConstant.COMMA;

public class UpdateQueryBuilder {
    public static final String UPDATE_QUERY = "UPDATE %s SET %s WHERE %s = %d";
    public static final String UPDATE_QUERY_WITHOUT_WHERE = "UPDATE %s SET %s";
    public static final String EQUALS = "=";
    private final TableClause tableClause;

    public UpdateQueryBuilder(Class<?> entity) {
        this.tableClause = new TableClause(entity);
    }

    /**
     * update 쿼리를 리턴한다.
     * @param entity update 될 최신 정보
     * @return update 쿼리
     */
    public String getQuery(Object entity, Long id) {

        List<String> columnNames = this.tableClause.columnNames();

        List<Field> fields = Arrays.stream(entity.getClass().getDeclaredFields()).collect(Collectors.toList());
        List<String> values = new ValueClauses(fields, entity).values();

        List<String> setClauses = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            setClauses.add(columnNames.get(i) + EQUALS + values.get(i));
        }

        if (id != null) {
            return String.format(UPDATE_QUERY, tableClause.name(), String.join(COMMA, setClauses), tableClause.primaryKeyName(), id);
        }

        return String.format(UPDATE_QUERY_WITHOUT_WHERE, tableClause.name(), String.join(COMMA, setClauses));
    }
}
