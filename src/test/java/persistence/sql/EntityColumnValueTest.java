package persistence.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class EntityColumnValueTest {

    @Test
    void stringColumnValueToQueryString() {
        // given
        EntityColumnValue entityColumnValue = new EntityColumnValue("test");

        // when
        String queryString = entityColumnValue.queryString();

        // then
        assertEquals("'test'", queryString);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void booleanColumnValueToQueryString(boolean value) {
        // given
        EntityColumnValue entityColumnValue = new EntityColumnValue(value);

        // when
        String queryString = entityColumnValue.queryString();

        // then
        assertEquals(queryString, value ? "1" : "0");
    }

    @Test
    void integerColumnValueToQueryString() {
        // given
        EntityColumnValue entityColumnValue = new EntityColumnValue(30);

        // when
        String queryString = entityColumnValue.queryString();

        // then
        assertEquals("30", queryString);
    }

    @Test
    void longColumnValueToQueryString() {
        // given
        EntityColumnValue entityColumnValue = new EntityColumnValue(1L);

        // when
        String queryString = entityColumnValue.queryString();

        // then
        assertEquals("1", queryString);
    }
}
