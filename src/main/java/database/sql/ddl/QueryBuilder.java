package database.sql.ddl;

import database.dialect.Dialect;

public class QueryBuilder {
    private static final QueryBuilder INSTANCE = new QueryBuilder();

    private QueryBuilder() {
    }

    public static QueryBuilder getInstance() {
        return INSTANCE;
    }

    public String buildCreateQuery(Class<?> clazz, Dialect dialect) {
        CreateQueryBuilder createQueryBuilder = new CreateQueryBuilder(clazz, dialect);
        return createQueryBuilder.buildQuery();
    }

    public String buildDeleteQuery(Class<?> clazz) {
        DropQueryBuilder dropQueryBuilder = new DropQueryBuilder(clazz);
        return dropQueryBuilder.buildQuery();
    }
}
