package persistence.sql.column;

import jakarta.persistence.GenerationType;

import java.util.Arrays;

public enum MysqlIdGeneratedStrategy implements IdGeneratedStrategy {
    IDENTITY("auto_increment", GenerationType.IDENTITY),
    ;
    private final String value;
    private final GenerationType generationType;

    MysqlIdGeneratedStrategy(String value, GenerationType generationType) {
        this.value = value;
        this.generationType = generationType;
    }

    public static MysqlIdGeneratedStrategy from(GenerationType strategy) {
        return Arrays.stream(values())
                .filter(mysqlIdGenerateStrategy -> mysqlIdGenerateStrategy.generationType.equals(strategy))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found GenerationType"));
    }

    @Override
    public GenerationType getGenerationType() {
        return generationType;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isAutoIncrement() {
        return "auto_increment".equals(value);
    }
}
