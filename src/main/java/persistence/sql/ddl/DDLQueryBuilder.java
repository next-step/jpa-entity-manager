package persistence.sql.ddl;

public interface DDLQueryBuilder {

    String createTableQuery(Class<?> clzz);

    String dropTableQuery(Class<?> clzz);

}
