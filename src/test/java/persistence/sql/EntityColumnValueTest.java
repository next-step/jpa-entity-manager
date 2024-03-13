package persistence.sql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EntityColumnValueTest {

    @Test
    void queryString() {
        // given
        EntityColumnValue entityColumnValue = new EntityColumnValue("test");

        // when
        String queryString = entityColumnValue.queryString();

        // then
        assertEquals("'test'", queryString);
    }
}
