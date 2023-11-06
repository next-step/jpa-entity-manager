package persistence.sql;

public abstract class QueryBuilder {
    protected final Dialect dialect;
    public QueryBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public String generateSQLQuery(Class<?> clazz) {
        throw new RuntimeException("Not implemented");
    }
}
