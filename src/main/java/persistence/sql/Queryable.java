package persistence.sql;

public interface Queryable {

    void applyToCreateTableQuery(StringBuilder query, Dialect dialect);

    boolean hasValue(Object entity);

    String getValue(Object entity);

    void bindValue(Object entity, Object value);

    String getName();

    String getDeclaredName();
}
