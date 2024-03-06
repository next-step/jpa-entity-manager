package persistence.sql.ddl.domain;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public enum Type {

    TINYINT(List.of(Byte.class, byte.class)),
    SMALLINT(List.of(Short.class, short.class)),
    INTEGER(List.of(Integer.class, int.class)),
    VARCHAR(List.of(String.class)),
    BIGINT(List.of(BigInteger.class, Long.class, long.class));

    private final List<Class<?>> supportedClasses;

    Type(List<Class<?>> classes) {
        this.supportedClasses = classes;
    }

    public static Type of(Class<?> type) {
        return Arrays.stream(values())
                .filter(t -> t.supportedClasses.contains(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + type));
    }

}
