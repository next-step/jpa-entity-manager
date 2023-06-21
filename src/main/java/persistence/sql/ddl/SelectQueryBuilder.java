package persistence.sql.ddl;

public abstract class SelectQueryBuilder {

    public String findAll(String tableName) {
        return String.format("select * from %s", tableName);
    }

    public String findById(String tableName, String columnName, String columnValue) {
        return String.format("select * from %s where %s=%s", tableName, columnName, columnValue);
    }
}
