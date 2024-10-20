package persistence.sql;

public interface Queryable {

    void applyToCreateTableQuery(StringBuilder query, Dialect dialect);

    boolean hasValue(Object entity);

    String getValue(Object entity);

    String getName();

    String getDeclaredName();
}
