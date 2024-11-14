package persistence.metadata;

import common.ErrorCode;
import jakarta.persistence.GenerationType;

import java.sql.Types;
import java.util.Arrays;

import static jakarta.persistence.GenerationType.IDENTITY;

public enum ColumnType {

    BIGINT(Long.class, Types.BIGINT),
    VARCHAR(String.class, Types.VARCHAR),
    INTEGER(Integer.class, Types.INTEGER)
    ;

    private final Class<?> javaType;
    private final int sqlType;

    ColumnType(Class<?> javaType, int sqlType) {
        this.javaType = javaType;
        this.sqlType = sqlType;
    }

    public static int getSqlType(Class<?> javaType) {
        return Arrays.stream(values())
                .filter(type -> type.javaType == javaType)
                .findFirst()
                .map(type -> type.sqlType)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_MATCH_TYPE.getErrorMsg()));
    }

    public static boolean isVarcharType(Class<?> javaType) {
        return javaType == String.class;
    }

    public static boolean isNotVarcharType(Class<?> javaType) {
        return !isVarcharType(javaType);
    }

    public static boolean isVarcharType(int sqlType) {
        return Types.VARCHAR == sqlType;
    }

    public static String getColumnTYpe(int type) {
        return switch(type) {
            case Types.BIGINT -> "bigint";
            case Types.VARCHAR -> "varchar";
            case Types.INTEGER -> "integer";
            default -> throw new IllegalArgumentException(ErrorCode.NOT_ALLOWED_DATATYPE.getErrorMsg());
        };
    }

    public static String getIdentifierGenerationType(GenerationType generationType) {
        return switch (generationType) {
            case IDENTITY -> "auto_increment";
            default -> throw new IllegalArgumentException(ErrorCode.NOT_ALLOWED_DATATYPE.getErrorMsg());
        };
    }

}
