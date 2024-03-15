package persistence.sql;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.entity.Person;

class EntityColumnsTest {

    @Test
    void getPrimaryColumn() {
        // given
        EntityColumns entityColumns = new EntityColumns(Person.class);

        // when
        EntityColumn primaryColumn = entityColumns.getPrimaryColumn();

        // then
        assertAll(
            () -> assertNotNull(primaryColumn),
            () -> assertEquals("id", primaryColumn.getColumnName()),
            () -> assertTrue(primaryColumn.isPrimary())
        );
    }
}
