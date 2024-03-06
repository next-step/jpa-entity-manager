package database.dialect;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MySQLDialectTest {

    @Test
    void convert() {
        assertConversion(Long.class, null, "BIGINT");
        assertConversion(String.class, 10, "VARCHAR(10)");
        assertConversion(String.class, 20, "VARCHAR(20)");
        assertConversion(Integer.class, null, "INT");
    }

    private void assertConversion(Class<?> entityType, Integer columnLength, String databaseType) {
        Dialect dialect = MySQLDialect.INSTANCE;

        assertThat(dialect.convertToSqlTypeDefinition(entityType, columnLength)).isEqualTo(databaseType);
    }
}
