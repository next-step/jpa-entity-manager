package database.sql.dml;

import java.util.Map;

public class QueryBuilder {
    private static final QueryBuilder INSTANCE = new QueryBuilder();

    private QueryBuilder() {
    }

    public static QueryBuilder getInstance() {
        return INSTANCE;
    }

    /* SELECT */
    public String buildSelectQuery(Class<?> entityClass) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityClass);
        return selectQueryBuilder.buildQuery();
    }

    public String buildSelectPrimaryKeyQuery(Class<?> entityClass, Long id) {
        SelectByPrimaryKeyQueryBuilder selectByPrimaryKeyQueryBuilder = new SelectByPrimaryKeyQueryBuilder(entityClass);
        return selectByPrimaryKeyQueryBuilder.buildQuery(id);
    }

    /* INSERT */
    public String buildInsertQuery(Class<?> entityClass, Map<String, Object> valueMap) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entityClass);
        return insertQueryBuilder.buildQuery(valueMap);
    }

    public String buildInsertQuery(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(entity.getClass());

        return insertQueryBuilder.buildQuery(columnValues(entity));
    }

    private Map<String, Object> columnValues(Object entity) {
        return new ColumnValueMap(entity).getColumnValueMap();
    }

    /* UPDATE */
    public String buildUpdateQuery(long id, Object entity) {
        Map<String, Object> map = new ColumnValueMap(entity).getColumnValueMap();
        return new UpdateQueryBuilder(entity.getClass()).buildQuery(id, map);
    }

    /* DELETE */
    public String buildDeleteQuery(Class<?> entityClass, Map<String, Object> conditionMap) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(entityClass);
        return deleteQueryBuilder.buildQuery(conditionMap);
    }

    public String buildDeleteQuery(Class<?> entityClass, Long id) {
        return buildDeleteQuery(entityClass, Map.of("id", id));
    }
}
