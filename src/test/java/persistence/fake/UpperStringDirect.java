package persistence.fake;

import jakarta.persistence.GenerationType;

public class UpperStringDirect extends FakeDialect {
    @Override
    public String getVarchar(int length) {
        return super.getVarchar(length).toUpperCase();
    }

    @Override
    public String getInteger() {
        return super.getInteger().toUpperCase();
    }

    @Override
    public String getBigInt() {
        return super.getBigInt().toUpperCase();
    }

    @Override
    public String getGeneratedType(GenerationType generationType) {
        return super.getGeneratedType(generationType).toUpperCase();
    }

    @Override
    public String notNull() {
        return super.notNull().toUpperCase();
    }

    @Override
    public String primaryKey(String columnName) {
        return super.primaryKey(columnName).toUpperCase();
    }

    @Override
    public String getFromTableQuery(String tableName) {
        return super.getFromTableQuery(tableName).toUpperCase();
    }
    @Override
    public String insert(String tableName) {
        return super.insert(tableName).toUpperCase();
    }
}
