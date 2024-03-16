package persistence.sql;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import persistence.sql.ddl.entity.Person;

class EntityColumnsTest {

    @Test
    void getPrimaryColumn() {
        // given
        EntityColumns entityColumns = new EntityColumns(Person.class);

        // when
        EntityColumn primaryColumn = entityColumns.getEntityIdColumn();

        // then
        assertAll(
            () -> assertNotNull(primaryColumn),
            () -> assertEquals("id", primaryColumn.getColumnName()),
            () -> assertTrue(primaryColumn.isPrimary())
        );
    }
}
