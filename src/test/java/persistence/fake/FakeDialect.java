package persistence.fake;

import jakarta.persistence.GenerationType;
import persistence.dialect.Dialect;
import persistence.dialect.h2.H2Dialect;

public class FakeDialect extends Dialect {
    @Override
    public String getVarchar(int length) {
        return String.format("varchar(%d)", length);
    }

    @Override
    public String getInteger() {
        return "integer";
    }

    @Override
    public String getBigInt() {
        return "bigint";
    }

    @Override
    public String getGeneratedType(GenerationType generationType) {
        if (generationType == GenerationType.IDENTITY) {
            return "generated by default as identity";
        }
        return "";
    }

    @Override
    public String notNull() {
        return "not null";
    }

    @Override
    public String primaryKey(String columnName) {
        return String.format("%s (%s)", "primary key", columnName);
    }


}
