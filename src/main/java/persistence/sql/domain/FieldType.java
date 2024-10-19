package persistence.sql.domain;

import java.util.Arrays;

public enum FieldType {
    LONG(Long.class, "BIGINT"),
    STRING(String.class, "VARCHAR"),
    INTEGER(Integer.class, "INT"),
    ;
    private final Class<?> type;
    private final String sqlType;

    FieldType(Class<?> type, String sqlType) {
        this.type = type;
        this.sqlType = sqlType;
    }

    public static FieldType from(Class<?> type) {
        return Arrays.stream(values())
                .filter(fieldType -> fieldType.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Type"));
    }

    public Class<?> getType() {
        return type;
    }

    public String getSqlType() {
        return sqlType;
    }

    public static String getSqlTypeByClass(Class<?> clazz) {
        for (FieldType fieldType : values()) {
            if (fieldType.getType().equals(clazz)) {
                return fieldType.getSqlType();
            }
        }
        throw new IllegalArgumentException("Unknown Type");
    }

}
