package persistence.dialect;

import static java.sql.Types.*;

public abstract class Dialect {

    public String columnType(int sqlTypeCode) {
        return switch (sqlTypeCode) {
            case INTEGER -> "integer";
            case BIGINT -> "bigint";
            case VARCHAR -> "varchar($l)";
            default -> throw new IllegalArgumentException("unknown type: " + sqlTypeCode);
        };
    }

    public String castColumnType(String template, Long size) {
        DdlType ddlType = new DdlType(template);
        return ddlType.getTypeName(size);
    }

    public String getCreateTableString() {
        return "create table";
    }

    public abstract String getNativeIdentifierGeneratorStrategy();

    public abstract String getPrimaryKeyStrategy(String name);
}
