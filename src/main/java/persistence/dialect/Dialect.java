package persistence.dialect;

import static java.sql.Types.*;

public abstract class Dialect {

    protected String columnType(int sqlTypeCode) {
        return switch (sqlTypeCode) {
            case INTEGER -> "integer";
            case BIGINT -> "bigint";
            case VARCHAR -> "varchar($l)";
            default -> throw new IllegalArgumentException("unknown type: " + sqlTypeCode);
        };
    }

    protected String castColumnType(String template, Long size) {
        DdlType ddlType = new DdlType(template);
        return ddlType.getTypeName(size);
    }

    abstract String getNativeIdentifierGeneratorStrategy();
}
