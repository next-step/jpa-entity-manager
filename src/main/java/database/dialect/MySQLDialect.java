package database.dialect;

import database.mapping.SqlTypes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.sql.Types.*;

public class MySQLDialect implements Dialect {
    private static final SqlTypes sqlTypes = new SqlTypes();
    private final Map<String, Integer> map;

    public static MySQLDialect INSTANCE = new MySQLDialect();

    private MySQLDialect() {
        map = new HashMap<>();
        register("java.lang.Long", BIGINT);
        register("java.lang.String", VARCHAR);
        register("java.lang.Integer", INTEGER);
    }

    private void register(String javaTypeName, Integer sqlType) {
        map.put(javaTypeName, sqlType);
    }

    @Override
    public String convertToSqlTypeDefinition(Class<?> type, Integer columnLength) {
        String sqlType = javaTypeToSqlType(type);
        if (sqlType.equals("VARCHAR")) {
            return sqlType + "(" + columnLength + ")";
        }
        return sqlType;
    }

    private String javaTypeToSqlType(Class<?> type) {
        String sqlType = javaTypeToSqlTypeName(type);
        if (sqlType == null) {
            throw new RuntimeException("Cannot convert type: " + type.getName());
        }
        return sqlType;
    }

    private String javaTypeToSqlTypeName(Class<?> type) {
        return sqlTypes.codeToName(map.get(type.getName()));
    }

    @Override
    public Object getFieldValueFromResultSet(ResultSet resultSet, String columnName, int sqlType) throws SQLException {
        switch (sqlType) {
            case BIGINT:
                return resultSet.getLong(columnName);
            case INTEGER:
                return resultSet.getInt(columnName);
            case VARCHAR:
                return resultSet.getString(columnName);
            default:
                throw new UnsupportedOperationException("아직 변환 지원 안하는 타입입나다: " + sqlType);
        }
    }

    @Override
    public String autoIncrementDefinition() {
        return "AUTO_INCREMENT";
    }

    @Override
    public String primaryKeyDefinition() {
        return "PRIMARY KEY";
    }

    @Override
    public String nullableDefinition(boolean nullable) {
        return nullable ? "NULL" : "NOT NULL";
    }
}
