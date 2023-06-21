package persistence.sql.ddl;

public abstract class SelectQueryBuilder {

    String findAll(String tableName) {
        return String.format("select * from %s", tableName);
    }

    String findById(String tableName, String columnName, String columnValue) {
        return String.format("select * from %s where %s=%s", tableName, columnName, columnValue);
    }
}
