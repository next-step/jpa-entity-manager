package persistence.sql.dml;

import persistence.sql.ddl.TableClause;
import persistence.sql.dml.clause.SetClauses;
import persistence.sql.dml.value.ValueClauses;

import java.util.ArrayList;
import java.util.List;

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
     * 테이블의 특정 row만 업데이트하는 쿼리를 리턴한다.
     * @param entity update 될 최신 정보
     * @param id row의 primary key
     * @return update 쿼리
     */
    public String getQuery(Object entity, Long id) {
        var setClauses = new SetClauses(entity.getClass()).query();
        return String.format(UPDATE_QUERY,
                tableClause.name(), String.join(COMMA, setClauses), tableClause.primaryKeyName(), id);
    }

    /**
     * 테이블의 전체 row를 업데이트하는 쿼리를 리턴한다.
     * @param entity update 될 최신 정보
     * @return update 쿼리
     */
    public String getQuery(Object entity) {
        var setClauses = new SetClauses(entity.getClass()).query();
        return String.format(UPDATE_QUERY_WITHOUT_WHERE, tableClause.name(), String.join(COMMA, setClauses));
    }
}
