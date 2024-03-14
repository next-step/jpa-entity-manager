package persistence.sql.dml.querybuilder;

import persistence.sql.ddl.clause.table.TableClause;
import persistence.sql.dml.clause.set.SetClauses;
import persistence.sql.dml.clause.value.ValueClauses;

import java.util.List;

import static persistence.sql.common.SqlConstant.COMMA;

public class UpdateQueryBuilder {
    public static final String UPDATE_QUERY = "UPDATE %s SET %s WHERE %s = %d";
    public static final String UPDATE_QUERY_WITHOUT_WHERE = "UPDATE %s SET %s";
    private final TableClause tableClause;

    public UpdateQueryBuilder(Class<?> clazz) {
        this.tableClause = new TableClause(clazz);
    }

    /**
     * 테이블의 특정 row만 업데이트하는 쿼리를 리턴한다.
     * @param entity update 될 최신 정보
     * @param id row의 primary key
     * @return update 쿼리
     */
    public String getQuery(Object entity, Long id) {
        List<String> setClauses = new SetClauses(tableClause.columnNames(), new ValueClauses(entity).values()).query();
        return String.format(UPDATE_QUERY,
                tableClause.name(), String.join(COMMA, setClauses), tableClause.primaryKeyName(), id);
    }

    /**
     * 테이블의 전체 row를 업데이트하는 쿼리를 리턴한다.
     * @param entity update 될 최신 정보
     * @return update 쿼리
     */
    public String getQuery(Object entity) {
        List<String> setClauses = new SetClauses(tableClause.columnNames(), new ValueClauses(entity).values()).query();
        return String.format(UPDATE_QUERY_WITHOUT_WHERE, tableClause.name(), String.join(COMMA, setClauses));
    }
}
